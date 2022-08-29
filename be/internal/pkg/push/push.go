package push

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/auth"
	"firebase.google.com/go/v4/db"
	"firebase.google.com/go/v4/messaging"
)

type Push struct {
	app *firebase.App
}

type sendPushV1Request struct {
	DeviceId string `json:"deviceId"`
	Payload  string `json:"payload"`
}

type HttpError struct {
	status  int
	message string
}

func (err *HttpError) Error() string {
	return err.message
}

func newHttpError(status int, message string) *HttpError {
	return &HttpError{
		status,
		message,
	}
}

func NewPush(ctx context.Context) *Push {
	conf := &firebase.Config{
		DatabaseURL: "https://push-508bd-default-rtdb.europe-west1.firebasedatabase.app/",
	}
	app, err := firebase.NewApp(ctx, conf)
	if err != nil {
		log.Fatalf("Error initializing app: %v\n", err)
	}

	return &Push{
		app,
	}
}

func (push *Push) SendPushV1Handler(w http.ResponseWriter, r *http.Request) {
	if os.Getenv("ENV") == "dev" {
		w.Header().Add("Access-Control-Allow-Origin", "http://127.0.0.1:5173")
		w.Header().Add("Access-Control-Allow-Headers", "authorization")
		w.Header().Add("Access-Control-Allow-Headers", "content-type")
	}

	if r.Method == http.MethodOptions {
		w.WriteHeader(http.StatusOK)
		return
	}

	userId, httpErr := push.getAuthenticatedUserId(r)
	if httpErr != nil {
		http.Error(w, httpErr.message, httpErr.status)
		return
	}

	var request sendPushV1Request
	err := json.NewDecoder(r.Body).Decode(&request)
	if err != nil {
		log.Printf("Failed to deserialize json: %v\n", err)
		http.Error(w, "Failed to deserialize request", http.StatusUnprocessableEntity)
		return
	}

	var notificationToken string
	err = push.getDatabase(r).NewRef(
		fmt.Sprintf("users/%s/pushRegistrations/%s/notificationToken", *userId, request.DeviceId),
	).Get(r.Context(), &notificationToken)
	if err != nil || notificationToken == "" {
		log.Printf("Failed to read from db: %v\n", err)
		http.Error(w, "device not found", http.StatusNotFound)
		return
	}

	log.Printf("Sending notification for user %s\n", *userId)

	message := messaging.Message{
		Data: map[string]string{
			"payload": request.Payload,
		},
		Token: notificationToken,
	}
	_, err = push.getMessaging(r).Send(r.Context(), &message)
	if err != nil {
		log.Printf("Failed to send notification: %v\n", err)
		http.Error(w, "Failed to send notification", http.StatusInternalServerError)
		return
	}
	fmt.Fprint(w, "{}")
}

func (push *Push) getAuthenticatedUserId(r *http.Request) (*string, *HttpError) {
	authHeader := r.Header.Get("Authorization")
	if !strings.HasPrefix(authHeader, "Bearer ") {
		log.Println("Invalid or no auth header")
		return nil, newHttpError(http.StatusUnauthorized, "Unauthorized")
	}

	token := authHeader[len("Bearer "):]
	fbToken, err := push.getAuth(r).VerifyIDTokenAndCheckRevoked(r.Context(), token)
	if err != nil {
		log.Printf("Token not valid: %v\n", err)
		return nil, newHttpError(http.StatusUnauthorized, "Unauthorized")
	}

	return &fbToken.UID, nil
}

func (push *Push) getAuth(r *http.Request) *auth.Client {
	auth, err := push.app.Auth(r.Context())
	if err != nil {
		log.Fatalf("Failed to create auth: %v\n", err)
	}
	return auth
}

func (push *Push) getDatabase(r *http.Request) *db.Client {
	db, err := push.app.Database(r.Context())
	if err != nil {
		log.Fatalf("Failed to create auth: %v\n", err)
	}
	return db
}

func (push *Push) getMessaging(r *http.Request) *messaging.Client {
	messaging, err := push.app.Messaging(r.Context())
	if err != nil {
		log.Fatalf("Failed to create messaging: %v\n", err)
	}
	return messaging
}

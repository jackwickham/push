package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"path"
	"path/filepath"
	"strings"

	"github.com/jackwickham/push/internal/pkg/push"
)

func main() {
	push := push.NewPush(context.Background())

	http.HandleFunc("/", rootHandler)
	http.HandleFunc("/assets/", assetsHandler)

	http.HandleFunc("/api/v1/send-push", push.SendPushV1Handler)

	log.Fatal(http.ListenAndServe("127.0.0.1:9785", nil))
}

func rootHandler(w http.ResponseWriter, r *http.Request) {
	servePublicFile(w, r, "index.html")
}

func assetsHandler(w http.ResponseWriter, r *http.Request) {
	clean_path := path.Clean(r.URL.Path)
	if strings.HasPrefix(clean_path, "/assets/") {
		servePublicFile(w, r, clean_path)
	} else {
		http.NotFound(w, r)
	}
}

func servePublicFile(w http.ResponseWriter, r *http.Request, file string) {
	body, err := os.ReadFile(filepath.Join("fe-dist", file))
	if err != nil {
		http.NotFound(w, r)
	}
	w.Write(body)
}

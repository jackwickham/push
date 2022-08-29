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
	http.HandleFunc("/favicon.ico", faviconHandler)

	http.HandleFunc("/api/v1/send-push", push.SendPushV1Handler)

	log.Fatal(http.ListenAndServe("0.0.0.0:9785", nil))
}

func rootHandler(w http.ResponseWriter, r *http.Request) {
	servePublicFile(w, r, "index.html")
}

func assetsHandler(w http.ResponseWriter, r *http.Request) {
	clean_path := path.Clean(r.URL.Path)
	if strings.HasPrefix(clean_path, "/assets/") {
		servePublicFile(w, r, clean_path)
	} else {
		log.Printf("Refusing to serve file %s\n", r.URL.Path)
		http.NotFound(w, r)
	}
}

func faviconHandler(w http.ResponseWriter, r *http.Request) {
	servePublicFile(w, r, "favicon.ico")
}

func servePublicFile(w http.ResponseWriter, r *http.Request, file string) {
	body, err := os.ReadFile(filepath.Join("fe-dist", file))
	if err != nil {
		log.Printf("File not found: %s\n", file)
		http.NotFound(w, r)
	}

	mimeType := inferMimeType(file)
	log.Printf("Serving file %s with type %s\n", file, mimeType)
	w.Header().Add("Content-Type", mimeType)
	w.Write(body)
}

func inferMimeType(file string) string {
	if strings.HasSuffix(file, ".css") {
		return "text/css"
	} else if strings.HasSuffix(file, ".js") {
		return "application/javascript"
	} else if strings.HasSuffix(file, ".html") {
		return "text/html"
	} else if strings.HasSuffix(file, ".ico") {
		return "image/x-icon"
	} else if strings.HasSuffix(file, ".png") {
		return "image/png"
	} else {
		return "text/plain"
	}
}

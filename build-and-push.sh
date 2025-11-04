#!/bin/bash

# Script para construir y subir imágenes a Docker Hub
# Usuario de Docker Hub
DOCKER_USERNAME="martinstiben"

# Nombres de las imágenes
BACKEND_IMAGE="sgh-backend"
FRONTEND_WEB_IMAGE="sgh-frontend-web"
FRONTEND_MOVIL_IMAGE="sgh-frontend-movil"

# Versión de las imágenes
VERSION="1.0"

echo "🚀 Construyendo y subiendo imágenes a Docker Hub..."

# Función para construir y subir imagen
build_and_push() {
    local service_name=$1
    local image_name=$2
    local dockerfile_path=$3
    local context_path=$4

    echo "📦 Construyendo imagen: $image_name"
    docker build -f "$dockerfile_path" -t "$DOCKER_USERNAME/$image_name:$VERSION" "$context_path"

    if [ $? -eq 0 ]; then
        echo "⬆️ Subiendo imagen: $DOCKER_USERNAME/$image_name:$VERSION"
        docker push "$DOCKER_USERNAME/$image_name:$VERSION"

        if [ $? -eq 0 ]; then
            echo "✅ Imagen $image_name subida exitosamente"
        else
            echo "❌ Error al subir la imagen $image_name"
            exit 1
        fi
    else
        echo "❌ Error al construir la imagen $image_name"
        exit 1
    fi
}

# Verificar si el usuario está logueado en Docker Hub
echo "🔐 Verificando login en Docker Hub..."
docker login

if [ $? -ne 0 ]; then
    echo "❌ Error: No se pudo hacer login en Docker Hub"
    exit 1
fi

# Construir y subir imágenes
build_and_push "backend" "$BACKEND_IMAGE" "Backend/SGH/Dockerfile" "Backend/SGH"
build_and_push "frontend-web" "$FRONTEND_WEB_IMAGE" "Frontend/frontend_Web/Dockerfile" "Frontend/frontend_Web"
build_and_push "frontend-movil" "$FRONTEND_MOVIL_IMAGE" "Frontend/SGH-Movil/Dockerfile" "Frontend/SGH-Movil"

echo "🎉 Todas las imágenes han sido subidas exitosamente a Docker Hub!"
echo ""
echo "📋 Imágenes disponibles:"
echo "  - $DOCKER_USERNAME/$BACKEND_IMAGE:$VERSION"
echo "  - $DOCKER_USERNAME/$FRONTEND_WEB_IMAGE:$VERSION"
echo "  - $DOCKER_USERNAME/$FRONTEND_MOVIL_IMAGE:$VERSION"
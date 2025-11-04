# Usar una imagen base de Node.js para Expo
FROM node:18-alpine

# Instalar dependencias del sistema para Expo
RUN apk add --no-cache git

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias con legacy peer deps para resolver conflictos
RUN npm install --legacy-peer-deps

# Copiar el resto del código
COPY . .

# Exponer el puerto de Expo
EXPOSE 19006

# Comando para ejecutar Expo
CMD ["npm", "start"]
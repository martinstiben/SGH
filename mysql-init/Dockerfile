FROM mysql:8.0

# Copiar el script de inicialización
COPY init.sql /docker-entrypoint-initdb.d/

# Configurar permisos
RUN chmod 644 /docker-entrypoint-initdb.d/init.sql

# Exponer el puerto
EXPOSE 3306
FROM adoptopenjdk:11-jre-hotspot

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://pg-3601cbff-cepa.a.aivencloud.com:28029/defaultdb?ssl=require
ENV SPRING_DATASOURCE_USERNAME=avnadmin
ENV SPRING_DATASOURCE_PASSWORD=AVNS_P5Hh_tD3dcckfHJp2vb

# Set environment variables for Eureka server
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=https

ENV JWT_SECRET_KEY=secret
ENV JWT_SERVICE_SECRET=secret-service
ENV JWT_EXPIRES_TIME=1000
# Set environment variables for Cloudinary
ENV CLOUDINARY_NAME=CEPA
ENV CLOUDINARY_API_KEY=212142713724382
ENV CLOUDINARY_API_SECRET=NA_z6IN7hUbfIWIdUjRCANtwjgI
ENV CLOUDINARY_WATERMARK=test
ENV ALLOWED_CONTENT_TYPES=image/jpeg,image/png,application/pdf,image/gif,video/mp4

# Set environment variables for Send mail
ENV SPRING_MAIL_HOST=smtp.gmail.com
ENV SPRING_MAIL_PORT=587
ENV SPRING_MAIL_USERNAME=akai792001@gmail.com
ENV SPRING_MAIL_PASSWORD=mjihqbluklrhpxkm

ENV METHOD=https

ENV VNPAY_RETURN_URL=http://localhost:8084/check-transaction
ENV VNPAY_REDIRECT_URL=https://capstone-fe-woad.vercel.app/my-course
ENV VNPAY_HASH_SECRET=BXZDCDKSBVAZKUHIHLJAKVGHDVMEJAFT
ENV VNP_TMNCODE=WWFB4G3P

VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ./target/courseservice-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]

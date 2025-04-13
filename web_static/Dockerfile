# 1. Apache 2.4 이미지 사용
FROM httpd:2.4

# 2. 웹 코드가 저장된 폴더를 Apache의 문서 루트에 복사
COPY web /usr/local/apache2/htdocs/

# 3. Apache 설정 파일을 수정하여 웹 코드를 제공하도록 설정
# 필요에 따라 설정 파일을 수정할 수 있습니다
# 예를 들어, Apache 설정 파일을 수정하여 특정 포트를 사용하도록 설정할 수 있습니다.
# RUN echo "Listen 8080" >> /usr/local/apache2/conf/httpd.conf

# 4. 컨테이너가 실행될 때 Apache를 포그라운드 모드로 실행
CMD ["httpd-foreground"]

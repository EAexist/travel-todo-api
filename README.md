<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/EAexist/travel-todo-api">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Travel Todo API</h3>

  <p align="center">
    API Server supporting Expo Mobile App
    <br />
    <br />
    <a href="https://travel-todo-git-staging-matchalab-project.vercel.app">View Demo</a>
  </p>
</div>


<!-- ABOUT THE APP -->
## About The App

Don't worry forgetting your passport expiracy. 

Don't be dig through emails at the hotel desk while clutching three suitcases.

Whether it's a passport, a portable charger, and bookings for all the museums, parks, hotels, and airbnbns, let travel todo track all them for you at once. All you have to do is enjoy the planning. 


<!-- ABOUT THE PROJECT -->
## About The Project

This API Server supports Subscription Killer service. 

* **RESTful API**

  Designed in RESTful structure and follows HTTP response protocol.
  
* **Built with Test-Driven Development**

  Developed using a TDD approach, achieving high code coverage and ensuring system reliability.

### Built With

* [![Spring][Spring]][Spring-url]
* [![Java][Java]][Java-url]


<!-- GETTING STARTED -->
## Getting Started

<a href="https://travel-todo-git-staging-matchalab-project.vercel.app">View Demo</a>
<!-- 
### Prerequisites

* Get Google Cloud Platform Info
  
  A.  Google Cloud Platform Project id : [Creating and managing projects](https://cloud.google.com/resource-manager/docs/creating-managing-projects)

  
  B.  Google Cloud Platform Service Account Credentials: [Create access credentials](https://developers.google.com/workspace/guides/create-credentials)

  
* Run Frontend Dev Server

  If you want to run a demo app using this api server, please refer to [trip-todo](https://github.com/EAexist/trip-todo).
  



### Installation

1. Get **A. Google Cloud Platfor Project id** and **B. Google Cloud Platform Service Account Credentials**. Refer to **Prerequisites**.
2. Clone the repo
   ```sh
   git clone https://github.com/EAexist/trip-todo-api.git
   ```
3. Create a new file `application.yml` at `src/main/resources` and enter following environment variables.
   ```sh
    spring:
        datasource:
            driver-class-name: org.postgresql.Driver
            url: jdbc:postgresql://localhost:5432/trip-todo
            username: guest
            password: hello_guest
            driver-class-name: org.postgresql.Driver
        jpa:
          hibernate:
            ddl-auto: update
        security:
            oauth2:
                resourceserver:
                    jwt:
                        kakao:
                            issuer-uri: https://kauth.kakao.com
                        google:
                            issuer-uri: https://accounts.google.com
        ai:
            vertex:
                ai:
                    gemini:
                        project-id: trip-todo
                        location: asia-northeast3
                        chat:
                            options:
                                model: gemini-2.0-flash-lite 
        cloud:
            gcp:
                project-id: 'YOUR GOOGLE CLOUD PROJECT ID'
                credentials:
                      location: 'PATH TO YOUR GOOGLE SERVICE ACCOUNT KEY FILE (Place it under src/main/resources)'
    cors:
      allowed-origins: http://localhost:8081 (or the url you're running client app)
   ```
4. Change git remote url to avoid accidental pushes to base project
   ```sh
   git remote set-url origin github_username/repo_name
   git remote -v # confirm the changes
   ```
5. Run the dev server. 
   ```sh
   bash local.sh
   ```
6. Your server will run in `http://localhost:5000` or on other port if 5000 is occupied. Check the console output for detailed information about the running server.  
    -->


<!-- LICENSE -->
## License

Copyright (c) 2026 Hyeon Pyo. All rights reserved.

No permission is granted for commercial use, distribution, or modification without explicit consent.


<!-- CONTACT -->
## Contact

Pyohyeon: hyeon.expression@gmail.com

Project Link: [https://github.com/EAexist/trip-todo-api](https://github.com/EAexist/trip-todo-api)


<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* [Google Gemini API](https://aistudio.google.com/welcome)
* [Amadeus](https://developers.amadeus.com/)
* [Best-README-Template](https://github.com/EAexist/Best-README-Template)


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Spring]: https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white
[Spring-url]: https://docs.spring.io/spring-boot/index.html
[Java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/

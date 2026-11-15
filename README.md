# LivDays — Campus Forum Platform

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F) ![Vue](https://img.shields.io/badge/Vue-3-42b883) ![MySQL](https://img.shields.io/badge/MySQL-8-4479A1) ![Docker](https://img.shields.io/badge/Docker-ready-2496ED)

A full-stack community platform for University of Liverpool students. Users sign up with an email verification code, then share posts with images, discuss them in threaded comments, like content, find popular topics, and message each other one-to-one.

> **👉 Latest code and full documentation are on the [`fix/local-run-and-bugfixes`](https://github.com/Jos1tar/livdays-forum/tree/fix/local-run-and-bugfixes) branch.**
> That branch has the architecture overview, setup guide, environment configuration, Docker deployment and roadmap.

## Tech Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 17 · Spring Boot 3.4 · MyBatis · PageHelper · JWT · BCrypt · Spring Mail |
| **Data & Storage** | MySQL 8 · Aliyun OSS |
| **Frontend** | Vue 3 (Composition API) · Vue Router · Element Plus · Axios · Vite |
| **DevOps** | Maven · Docker |

## Highlights

- **Stateless JWT authentication** through a custom Spring MVC interceptor. Each user's identity comes from the token, and passwords are hashed with BCrypt.
- **Email-verified registration** with time-limited one-time codes sent over SMTP.
- **One-to-one private messaging** with separate unread counters for each side and soft-deleted conversations that come back when a new message arrives.
- **Like de-duplication and counters kept in sync**, enforced by database unique keys and transactions.
- **A unified `{code, msg, data}` API contract** with global exception handling.
- **Environment-based configuration** and a Docker image ready for deployment.

## Repository Layout

```
forumParent/forum/        Spring Boot backend
FrontEnd/Frondend-src/    Vue 3 frontend
forumCopy.sql             MySQL schema and sample data
Deploy_backend/           Deployment files
```

# Virtual Wardrobe

**An AI-powered virtual wardrobe and outfit advisor, inspired by Cher Horowitz's iconic computerized closet from *Clueless* (1995).**

Instead of picking outfits at random, Virtual Wardrobe helps you build, store, and evaluate your outfits — and then asks an AI model whether the outfit actually works, both stylistically and on you specifically.

> This repository contains the **backend (REST API)**, built with Spring Boot. The frontend lives in a separate repo — see [Frontend](#frontend) below.

---

## Table of Contents
- [Why I Built This](#why-i-built-this)
- [Features](#features)
- [How the AI Fits In](#how-the-ai-fits-in)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Frontend](#frontend)
- [What I Learned](#what-i-learned)
- [Roadmap](#roadmap)
- [License](#license)
- [Author](#author)

---

## Why I Built This

I've always thought Cher's digital wardrobe in *Clueless* was way ahead of its time, and I wanted to build my own version of it. Beyond the fun factor, I used this project as a hands-on way to properly learn **Spring Boot**, and it became my first real web API project — and my first time integrating an actual AI model into something I built.

The core idea: outfit creation shouldn't be a guessing game. Virtual Wardrobe lets you organize your clothes properly and then get real feedback on the outfits you put together, instead of just hoping they work.

## Features

- Upload, update, and delete clothing items
- Browse and filter your wardrobe by category, occasion, season, comfort level — or view everything at once
- Create outfits from your existing items
- View and edit previously created outfits
- AI styling advice on a selected outfit (does it work as a combination?)
- Personalized suitability analysis — the AI evaluates how well the outfit suits you, based on a photo you provide
- Outfit analysis history — every past AI evaluation is saved and can be revisited
- Secure authentication via JWT

## How the AI Fits In

Two distinct AI-backed features, powered by **Google Gemini**:

1. **Outfit Advice** — once items are selected for an outfit, the backend sends a structured description of the combination to Gemini and returns styling feedback (does this combination work, what could be improved, etc.)
2. **Suitability Analysis** — combines the chosen outfit with a user-uploaded photo, and asks Gemini to analyze how well that specific outfit suits that specific person. The result is stored so it can be reviewed later in the outfit analysis history.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL |
| Security | Spring Security + JWT (`jjwt`) |
| AI | Google Gemini (`google-genai` SDK) |
| Email | Spring Boot Starter Mail |
| Build Tool | Maven |

## Architecture

The backend follows a standard layered Spring Boot architecture:

```
Controller  ->  Service  ->  Repository  ->  Entity (JPA)
                  |
            AI Service Layer (Gemini integration)
                  |
         Security Layer (JWT filters, Spring Security config)
```

- **Controllers** expose REST endpoints for wardrobe items, outfits, and AI analysis.
- **Services** hold the business logic, including building prompts/requests for Gemini.
- **Repositories** (Spring Data JPA) handle persistence to MySQL.
- **Security** is handled through a custom JWT filter chain integrated with Spring Security.

## Getting Started

### Prerequisites
- JDK 17+
- Maven (or use the included wrapper, `./mvnw`)
- A running MySQL instance
- A Gemini API key (from [Google AI Studio](https://aistudio.google.com/))

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Gannah211/Virtual_Wardrobe.git
   cd Virtual_Wardrobe
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE virtual_wardrobe;
   ```

3. **Configure your environment**

   Create (or edit) `src/main/resources/application.properties`:
   ```properties
   # Database
   spring.datasource.url=jdbc:mysql://localhost:3306/virtual_wardrobe
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   spring.jpa.hibernate.ddl-auto=update

   # JWT
   jwt.secret=your_jwt_secret_key
   jwt.expiration=86400000

   # Gemini API
   gemini.api.key=your_gemini_api_key

   # Mail (if using email verification / notifications)
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_email_app_password
   ```
   > Note: property names above are placeholders — match them to whatever keys your `@Value`/config classes actually read.

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The API will start on `http://localhost:8080` by default.

## Frontend

The frontend is a plain HTML/CSS/JS app, built with AI-assisted tooling, that lives in a separate repository:

➡️ **Frontend repo:** [https://github.com/Gannah211/Virtual_Wardrobe_Frontend](https://github.com/Gannah211/Virtual_Wardrobe_Frontend)

## What I Learned

This project was full of firsts for me:
- My **first real web API project** — moving from tutorials to an actual full backend
- My **first time integrating an AI model** into a project, and figuring out how to structure prompts/requests around real user data
- My **first time implementing JWT authentication**, which meant learning Spring Security filters from scratch — definitely the hardest part to wrap my head around at first, but very satisfying once it clicked

## Roadmap

- [ ] Add automated unit/integration tests
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Deploy a live demo

## Author

**Gannah Mohamed**
[GitHub](https://github.com/Gannah211)

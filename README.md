[🇫🇷 Lire en français](README.fr.md) | [🇬🇧 Read in English](README.md)

# mrisk – Type 2 Diabetes Risk Analysis Microservice

---
**Project**: MicroDiab  
**Organization**: MediLabo Solutions  
**Author**: michaudj  
**Date**: January 2026  
**Version**: 1.0.0
---

## 📌 Role
The **mrisk** microservice is a medical analysis back-end microservice responsible for evaluating the level of risk of 
type 2 diabetes for a patient, for the **MicroDiab** application. The **mrisk** microservice is a **stateless** service 
and does not have its own database.

It analyzes:
* Patient data (age, gender) provided by the **mpatient** microservice.
* Medical notes provided by the **mnotes** microservice.

The result is a risk report calculated according to defined medical rules.

**Key Features**:
- Calculation of the **level of risk of type 2 diabetes**.
- Inter-microservice communication via **Feign Client**.
- Interactive documentation via **Swagger UI** and **Javadoc**.
- Distributed tracing with **Zipkin** and **Micrometer Tracing**.
- Containerized with **Docker** (compatible with `docker-compose`).

---

## 📦 Dependencies
**Internal Microservices**

| Microservice      | Role                              | Port |
|-------------------|-----------------------------------|------|
| **eureka-server** | Service discovery                 | 9102 |
| **zipkin**        | Distributed tracing               | 9411 |
| **mgateway**      | API Gateway + Authentication      | 9010 |
| **mpatient**      | Patient data                      | 9001 |
| **mnotes**        | Medical notes                     | 9002 |

**Communication**: Feign (calls from `clientui` -- calls to `mpatient` and `mnotes`)
**Tests**: JaCoCo report available in `target/`
**Network**: microdiab-network (shared with other microservices).

**Security**: The gateway applies centralized authentication, but each microservice remains independently secured to prevent accidental exposure.
* **External Security**: Spring Security (centralized authentication via `mgateway`)
* **Internal Security**: Spring Security (Basic Auth for inter-microservice communications)

---

## 📊 Monitoring and Documentation
| Tool       | URL                                                                  | Description        |
|------------|----------------------------------------------------------------------|--------------------|
| Actuator   | [http://localhost:9003/actuator](http://localhost:9003/actuator)     | Metrics and health |
| Swagger UI | [http://localhost:9003/swagger-ui](http://localhost:9003/swagger-ui) | API Documentation  |
| Javadoc    | [http://localhost:9003/apidocs](http://localhost:9003/apidocs)       | Java Documentation |

---

## 🛠 Prerequisites
- Java 21
- Spring Boot 3.5.8
- Spring Cloud 2025.0.0
- Maven
- Docker
- Environment variables:
    - `EUREKA_SERVER_HOST=eureka-server`
    - `EUREKA_SERVER_PORT=9102`

---

## 🚀 Main Endpoints
| Method | Endpoint        | Description                             |
|--------|-----------------|-----------------------------------------|
| GET    | `/risk/{patId}` | Delivers the diabetes risk level report |

**patId** corresponds to the patient's unique identifier.

---

## 🧠 Diabetes Risk Analysis
### 🎚️ Possible Risk Levels
A patient can be assigned one of the following four risk levels:
* **None**: No risk
* **Borderline**: Limited risk
* **In Danger**: Danger
* **Early onset**: Early onset

### 🧪 Trigger Terms Analyzed
The following terms, **in French**, are searched for in medical notes :
* Hemoglobin A1C
* Microalbumin
* Height
* Weight
* Smoker
* Abnormal
* Cholesterol
* Dizziness
* Relapse
* Reaction
* Antibodies

### 📏 Calculation Rules
* 🟢 **No Risk** (None):
    * No medical note contains trigger terms.

* 🟡 **Limited Risk** (Borderline):
    * Between 2 and 5 trigger terms.
    * Patient older than 30 years.

* 🟠 **Danger** (In Danger):
    * Under 30 years: Male ≥ 3 terms // Female ≥ 4 terms
    * Over 30 years: 6 or 7 terms

* 🔴 **Early Onset** (Early onset):
    * Under 30 years: Male ≥ 5 terms // Female ≥ 7 terms
    * Over 30 years: ≥ 8 terms

---

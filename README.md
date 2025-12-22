# Crypto Arb Core

Java + Spring Boot backend for crypto arbitrage.
Fetches real-time prices from major exchanges, analyzes price discrepancies, computes profitable opportunities, and executes trading orders.

### Table of Contents

* Features
* Installation
* Configuration
* Technologies
* Disclaimer
* Contact

### Features

* Real-time price fetching from multiple exchanges (e.g., Binance, WhiteBIT, etc.)
* Arbitrage opportunity detection (cross-exchange and triangular)
* Profitability calculation including fees, slippage, and execution risks
* Automated order execution
* REST API for monitoring and control
* Logging and metrics for performance tracking

### Installation

#### Clone the Repository

git clone https://github.com/chernovprog/crypto-arb-core.git

#### Run the Application

`./gradlew bootRun`

(Windows: `gradlew.bat bootRun`)

Or build and run the JAR:

`java -jar target/crypto-arb-core-0.0.1-SNAPSHOT.jar`

### Configuration

Add environment variables from application.yaml using one of your favorite approaches

### Technologies

* Java 17+
* Spring Boot 4.0.1
* Spring Data JPA 
* Spring Security
* Spring Web MVC
* Spring Boot Configuration Processor
* PostgreSQL 18
* Lombok
* Gradle
* WebClient for API calls
* WebSockets for real-time data streams

###  Disclaimer

Cryptocurrency trading involves significant risk and can result in the loss of your invested capital. This software is provided for educational and research purposes only. Use at your own risk. The authors are not responsible for any financial losses incurred. Always perform your own due diligence and consider consulting financial professionals.

**Important:** Never use real funds until you have thoroughly tested the system in a simulation environment. Arbitrage opportunities can disappear quickly, and execution delays or fees can turn profits into losses.

### Contact

* **Author:** Andrii Chernov

Thank you for checking out Crypto Arb Core! If you find it useful, consider starring the repo ⭐.

# Personal Finance App - Project Structure Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Project Directory Structure](#project-directory-structure)
3. [Layer Responsibilities](#layer-responsibilities)
4. [Database Schema (Room)](#database-schema-room)
5. [Firebase Structure](#firebase-structure)
6. [Sync Strategy](#sync-strategy)
7. [Notification System](#notification-system)
8. [Data Export Implementation](#data-export-implementation)
9. [Phase-by-Phase Implementation Guide](#phase-by-phase-implementation-guide)
10. [Coding Conventions](#coding-conventions)

---

## Architecture Overview

This project follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern to ensure:
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Testability**: Business logic is independent of UI and frameworks
- **Maintainability**: Changes in one layer don't affect others
- **Scalability**: Easy to add new features without breaking existing code

### Three Main Layers:

#### 1. Data Layer (Outermost)
- **Responsibilities**: Data sources, API calls, database operations
- **Components**: Room entities, DAOs, Firebase services, Retrofit APIs, Repository implementations
- **Purpose**: Provides and manages data from various sources (local DB, cloud, APIs)

#### 2. Domain Layer (Middle)
- **Responsibilities**: Business logic, use cases, domain models
- **Components**: Repository interfaces, use cases, business rules
- **Purpose**: Contains app's business rules, independent of UI and data sources

#### 3. Presentation Layer (Innermost)
- **Responsibilities**: UI rendering, user interactions, state management
- **Components**: Compose screens, ViewModels, navigation
- **Purpose**: Presents data to users and handles user interactions

### Data Flow:

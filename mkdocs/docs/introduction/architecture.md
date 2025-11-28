# RespondNow Project Architecture Documentation

---

## Overview

RespondNow is an open-source incident management platform that integrates seamlessly with Slack. It enables teams to manage incidents within their existing communication tool, enhancing collaboration and response times. The system consists of a Java-based backend server, a React-based frontend portal, and a Slack application. It is containerized using Docker and deployable via Helm charts on Kubernetes clusters.

---

## Why

**Purpose and Goals:**
- **Incident Management:** Provide a robust platform for managing incidents efficiently.
- **Slack Integration:** Leverage Slack as the main interface for incident management, minimizing context switching.
- **Open Source:** Build a community-driven, easily extensible, and customizable incident management tool.
- **Collaboration:** Enhance team collaboration during incident responses with real-time updates and communication.

---

## What

**Project Components:**

1. **Backend Server:**
   - **Tech Stack:** Java, Spring Boot, MongoDB
   - **Responsibilities:** Handle business logic, data storage, and API endpoints for incident management.
   - **Key Classes and Methods:**
     - [IncidentService](./../../../server/src/main/java/io/respondnow/service/incident/IncidentService.java): Interface defining incident lifecycle operations.
     - [IncidentServiceImpl](./../../../server/src/main/java/io/respondnow/service/incident/IncidentServiceImpl.java): Implementation with methods for creating, updating, deleting, and acknowledging incidents.
     - [IncidentController](./../../../server/src/main/java/io/respondnow/controller/IncidentController.java): REST controller exposing incident management API endpoints.
     - [IncidentRepository](./../../../server/src/main/java/io/respondnow/repository/IncidentRepository.java): MongoDB repository for incident data.
     - [ProjectService](./../../../server/src/main/java/io/respondnow/service/hierarchy/ProjectService.java): Interface defining project-related operations.
     - [ProjectServiceImpl](./../../../server/src/main/java/io/respondnow/service/hierarchy/ProjectServiceImpl.java): Implementation of ProjectService.
     - [ProjectRepository](./../../../server/src/main/java/io/respondnow/repository/ProjectRepository.java): MongoDB repository for project data.
   - **Incident API Endpoints:**
     - `POST /api/incident` - Create a new incident
     - `GET /api/incident/{id}` - Get incident details
     - `GET /api/incidents` - List incidents with filtering and pagination
     - `DELETE /api/incident/{id}` - Soft delete an incident
     - `PUT /api/incident/{id}/acknowledge` - Acknowledge an incident
     - `PUT /api/incident/{id}/status` - Update incident status
     - `PUT /api/incident/{id}/severity` - Update incident severity
     - `PUT /api/incident/{id}/summary` - Update incident summary
     - `POST /api/incident/{id}/comment` - Add comment to incident timeline

2. **Frontend Portal:**
   - **Tech Stack:** React, TypeScript, Webpack, TanStack Query
   - **Responsibilities:** Provide a web-based dashboard for monitoring and managing incidents.
   - **Key Components:**
     - `IncidentsView` - Dashboard displaying incident list with filters and search
     - `IncidentDetailsView` - Detailed view of a single incident with timeline
     - `CreateIncidentModal` - Modal form for creating new incidents
     - `IncidentActions` - Action buttons for acknowledge, status update, and delete
     - `SeverityBadge`, `StatusBadge` - Visual indicators for incident severity and status
     - `SideNav` - Navigation component
   - **API Hooks:**
     - `useListIncidentsQuery` - Fetch paginated incident list
     - `useGetIncidentQuery` - Fetch single incident details
     - `useCreateIncidentMutation` - Create new incident
     - `useDeleteIncidentMutation` - Delete incident
     - `useAcknowledgeIncidentMutation` - Acknowledge incident
     - `useUpdateIncidentStatusMutation` - Update incident status
     - `useUpdateIncidentSeverityMutation` - Update incident severity

3. **Slack App:**
   - **Responsibilities:** Allow users to perform incident management tasks directly from Slack.
   - **Integration:** Use Slack APIs to interact with the backend server for incident operations.

4. **Containerization:**
   - **Docker:** Dockerfiles for building and deploying the application in containers.
   - **Helm Charts:** For Kubernetes deployments.

---

## Where

**Project Structure:**

- **Backend Server:**
  - Located in `server/src/main/java/io/respondnow/...`.
  - Key Directories:
    - `service/hierarchy`: Contains services like [ProjectService](./../../../server/src/main/java/io/respondnow/service/hierarchy/ProjectService.java), [ProjectServiceImpl](./../../../server/src/main/java/io/respondnow/service/hierarchy/ProjectServiceImpl.java).
    - `model/hierarchy`: Contains data models like [Project](./../../../server/src/main/java/io/respondnow/model/hierarchy/Project.java).
    - `repository`: Contains repository interfaces.

- **Frontend Portal:**
  - Located in `portal/src/...`.
  - Key Directories:
    - `components`: Reusable UI components.
    - `views`: Main views like `GettingStarted`, `IncidentDetails`.
    - `services`: API service hooks.
    - `context`: Context providers for global state management.

- **Slack App:**
  - Not explicitly detailed in the provided snippets, but it would typically reside in its own directory with integration points in the backend server.

- **Containerization:**
  - Dockerfiles located in [server/Dockerfile](./../../../server/Dockerfile) and [server/src/main/docker/Dockerfile](./../../../server/src/main/docker/Dockerfile).
  - Helm charts stored in a separate repository as referenced in the documentation.

---

## How

**Key Processes and Workflows:**

1. **Incident Lifecycle Management:**

   - **Create Incident:**
     - Method: `IncidentServiceImpl.createIncident`
     - Workflow: Validate input -> Create incident record -> Add timeline entry -> Optionally create Slack channel -> Save to repository

   - **Acknowledge Incident:**
     - Method: `IncidentServiceImpl.acknowledgeIncident`
     - Workflow: Fetch incident -> Update status to "Acknowledged" -> Add timeline entry -> Save changes

   - **Update Incident Status:**
     - Method: `IncidentServiceImpl.updateStatus`
     - Workflow: Fetch incident -> Validate status transition -> Update status -> Add timeline entry -> Save changes
     - Supported statuses: `Started`, `Acknowledged`, `Investigating`, `Identified`, `Mitigated`, `Resolved`

   - **Update Incident Severity:**
     - Method: `IncidentServiceImpl.updateSeverity`
     - Workflow: Fetch incident -> Update severity -> Add timeline entry -> Save changes
     - Supported severities: `SEV0` (Critical), `SEV1` (Major), `SEV2` (Minor)

   - **Delete Incident:**
     - Method: `IncidentServiceImpl.deleteIncident`
     - Workflow: Fetch incident -> Soft delete by setting `removed` flag -> Add timeline entry -> Save changes

   - **Add Comment:**
     - Method: `IncidentServiceImpl.addComment`
     - Workflow: Fetch incident -> Add comment to timeline -> Save changes

2. **Project Management:**
   - **Create Project:** `ProjectServiceImpl.createProject` - Validate existence -> Save to repository
   - **Delete Project:** `ProjectServiceImpl.deleteProject` - Soft delete by setting `removed` flag
   - **Retrieve Projects:** `ProjectServiceImpl.findById`, `getAllProjects`

3. **Timeline Tracking:**
   - All incident changes are recorded in a timeline with:
     - Change type (Status, Severity, Comment, etc.)
     - Previous and current state
     - User who made the change
     - Timestamp

4. **Deployment:**
   - Docker: Multi-stage Dockerfile to build and run the application.
   - Kubernetes: Helm charts for deploying on Kubernetes clusters.

---

## When

**Development and Deployment Timeline:**
- **Initial Development:**
  - Backend server and frontend portal development started with fundamental features.
  - Slack integration was added to enhance real-time communication.

- **Milestones:**
  - **Release 0.1.0:** Initial cut of the platform with core features and Slack integration.
  - **Release 0.2.0:** Full incident lifecycle management (create, acknowledge, update, delete) via web portal.
  - **Subsequent Releases:** Incremental improvements, bug fixes, and feature additions based on community feedback and internal roadmap.

- **Deployment Schedule:**
  - Continuous Integration (CI) setup to ensure code quality and automated testing.
  - Regular deployments to Kubernetes clusters using Helm charts for consistent updates and scaling.

---

## Where

**Deployment Environment:**
- The RespondNow platform is designed to be deployed on Kubernetes clusters, ensuring high availability and scalability.
- Docker is used for containerizing the application, making it portable and easy to deploy across different environments.
- Helm charts are used for managing Kubernetes deployments, providing a standardized way to deploy, upgrade, and maintain the application.

---

## Conclusion

This architecture document provides a comprehensive overview of the RespondNow project, detailing its purpose, components, structure, workflows, and deployment strategies. By leveraging modern technologies and best practices, RespondNow aims to be a powerful tool for incident management, fostering collaboration and efficiency in handling incidents.

For more detailed information, please refer to the specific documentation and code comments within the project repositories.
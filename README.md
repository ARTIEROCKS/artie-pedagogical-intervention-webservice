# ARTIE Pedagogical Intervention Web Service

This service provides endpoints for managing pedagogical software data, solutions to exercises, and learning progress tracking.

## Endpoints

### Pedagogical Software

#### Store Pedagogical Software Data
- **URL**: `/api/v1/pedagogicalsoftware/sendPedagogicalSoftwareData`
- **Method**: `POST`
- **Description**: Stores the pedagogical software data and triggers interventions based on the data.
- **Parameters**:
  - `data`: JSON string containing the elements in the workspace.
- **Response**: JSON string representing the added data.
- **Status Codes**:
  - `201 Created`: Data successfully stored.

#### Update Answered Need Help
- **URL**: `/api/v1/pedagogicalsoftware/update/answeredNeedHelp`
- **Method**: `PUT`
- **Description**: Updates if the user answered yes to needing help and triggers interventions accordingly.
- **Parameters**:
  - `id`: ID of the pedagogical software data.
  - `answeredNeedHelp`: Boolean indicating if the user needs help.
- **Response**: JSON string representing the updated data.
- **Status Codes**:
  - `202 Accepted`: Update successful.

#### Store Pedagogical Software Solution
- **URL**: `/api/v1/pedagogicalsoftware/sendPedagogicalSoftwareSolution`
- **Method**: `POST`
- **Description**: Stores the solution to an exercise.
- **Parameters**:
  - `data`: JSON string containing the solution to the exercise.
- **Response**: JSON string representing the added solution.
- **Status Codes**:
  - `201 Created`: Solution successfully stored.

#### Get Finished Exercises by User ID
- **URL**: `/api/v1/pedagogicalsoftware/finishedExercises`
- **Method**: `GET`
- **Description**: Retrieves finished exercises by user ID.
- **Parameters**:
  - `userId`: ID of the user.
- **Response**: Array of Exercise objects.
- **Status Codes**:
  - `302 Found`: Exercises found.

#### Get Finished Exercises by Student ID
- **URL**: `/api/v1/pedagogicalsoftware/finishedExercisesByStudentId`
- **Method**: `GET`
- **Description**: Retrieves finished exercises by student ID.
- **Parameters**:
  - `studentId`: ID of the student.
- **Response**: Array of Exercise objects.
- **Status Codes**:
  - `302 Found`: Exercises found.

#### Validate Finished Exercise by Pedagogical Data ID
- **URL**: `/api/v1/pedagogicalsoftware/finishedExercises/validate`
- **Method**: `GET`
- **Description**: Sets the validated value in a finished exercise.
- **Parameters**:
  - `pedagogicalDataId`: ID of the pedagogical software data.
  - `validated`: Integer representing validation status.
- **Response**: None.
- **Status Codes**:
  - `302 Found`: Exercise found.

#### Get Solutions by User ID
- **URL**: `/api/v1/pedagogicalsoftware/solutions`
- **Method**: `GET`
- **Description**: Retrieves solutions by user ID.
- **Parameters**:
  - `userId`: ID of the user.
- **Response**: Array of Solution objects.
- **Status Codes**:
  - `302 Found`: Solutions found.

#### Delete Solution by ID
- **URL**: `/api/v1/pedagogicalsoftware/solutions/delete`
- **Method**: `GET`
- **Description**: Deletes a solution by ID.
- **Parameters**:
  - `solutionId`: ID of the solution.
- **Response**: None.
- **Status Codes**:
  - `202 Accepted`: Solution successfully deleted.

#### Get Learning Progress by Exercise and Student
- **URL**: `/api/v1/pedagogicalsoftware/learningProgress/getByExerciseAndStudent`
- **Method**: `GET`
- **Description**: Retrieves learning progress by student and exercise.
- **Parameters**:
  - `studentId`: ID of the student.
  - `exerciseId`: ID of the exercise.
- **Response**: Array of LearningProgress objects.
- **Status Codes**:
  - `302 Found`: Learning progress found.

### Sensor

#### Send Sensor Data
- **URL**: `/api/v1/sensor/sendSensorData`
- **Method**: `POST`
- **Description**: Receives sensor data and triggers actions based on user authentication.
- **Parameters**:
  - `securitySensorData`: SecuritySensorData object containing sensor data, user credentials, and student information.
- **Response**: None.
- **Status Codes**:
  - `201 Created`: Data successfully received and processed.

## Dependencies

- **Intervention Service**: Handles interventions triggered by pedagogical software data.
- **Pedagogical Software Service**: Manages pedagogical software data.
- **Pedagogical Software Solution Service**: Manages solutions to exercises.
- **Emotional State Service**: Manages emotional state data.
- **Security Service**: Manages user authentication and security operations.

## Cross-Origin Resource Sharing (CORS)

Cross-Origin Resource Sharing (CORS) is enabled for all origins and headers.

## Note

Ensure proper authentication and authorization mechanisms are implemented for sensitive endpoints.

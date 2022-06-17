# Roles and Permissions 

## Users and groups
Intuit Tank uses user roles to dictate which users can perform a given action. The administrator determines which roles are assigned to which users via the Admin section. The default role assigned to users is "User" but each Intuit Tank user may have more than one role.

??? tip "Tip: Assigning Groups"
    `Administration` section below has detailed instructions on Assigning groups.


## Roles 

| Role Name | Role Description | 
| :---------| :----------------|
| Guest | `Guest` is a default role assigned to all users. Guests are capable of viewing any type of entity on Intuit Tank, however they are unable to create or edit any entities.|
| User | `User` is another default role assigned to all users. Users can create Scripts, Projects, Filters, and Filter Groups, as well as upload Data Files; however, Users may only edit entities of which they are the owner.|
| Project Manager | Intuit Tank users with the `Project Manager` role are responsible for maintaining Intuit Tank Projects. While they are unable to create Scripts or Filters/Filter Groups, they have the ability to make edits to other users' Projects as well as control that Project's Job Queue.|
| Script Manager | Intuit Tank users with the `Script Manager` role are responsible for upkeep of Scripts, Filters, and Filter Groups. Script Managers are unable to create Projects or control jobs; however, they have permission to edit other users' Scripts and Filters/Filter Groups.|
| Job Manager | Intuit Tank users with the `Job Manager` role are responsible for controlling jobs in the Agent Tracker/Job Queue. For example, they can choose to run or delete a job in the Job Queue, however, they may not create or edit the Project.|

## Permissions Matrix



| Capability | Member Role      | Create          | Delete         |  Modify        |  Read             | 
| :---------:| :---------:      | :-------:       |:-------:       |:-------:       |:-------:          |
| Projects | Guest | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Projects | User | :white_check_mark: |:crossed_swords:|:crossed_swords:|:crossed_swords:   |
| Projects | Script Manager | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Projects | Project Manager | :white_check_mark: |:white_check_mark: |:white_check_mark: |:white_check_mark:   |
| Projects | Job Manager | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Scripts | Guest | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Scripts | User | :white_check_mark: |:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Scripts | Script Manager | :white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:   |
| Scripts | Project Manager | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Scripts | Job Manager | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Filters | Guest | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Filters | User | :white_check_mark: |:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Filters | Script Manager  | :white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:   |
| Filters | Project Manager | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Filters | Job Manager  | :crossed_swords:|:crossed_swords:|:crossed_swords:|:white_check_mark:   |
| Data Files | Guest | :crossed_swords:|:crossed_swords:|:material-null:|:white_check_mark:   |
| Data Files | User | :white_check_mark: |:crossed_swords:|:material-null:|:white_check_mark:   |
| Data Files | Script Manager | :white_check_mark: |:white_check_mark: |:material-null:|:white_check_mark:   |
| Data Files | Project Manager | :white_check_mark: |:white_check_mark: |:material-null:|:white_check_mark:   |
| Data Files | Job Manager | :crossed_swords:|:crossed_swords:|:material-null:|:white_check_mark:   |

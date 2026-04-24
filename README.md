# IMS

IMS stands for Inventory Management System for End User Equipment. This project was developed by Group B19 using Spring Boot MVC and MySQL.

## Group Members

- `24RP04887 - Elie Iradukunda`
- `24RP00463 - Ufiteyesu Esther`

## Project Purpose

This system helps to manage end-user equipment such as laptops, desktops, mobile phones, and tablets. It supports asset registration, issuing equipment, returning equipment, reporting, audit trail, and account login.

## SysAdmin Credentials

- Username: `24RP04887`
- Password: `24RP00463`

## Important Files

- Database file to import: `ims.sql`
- Eclipse project folder to import: `IMS`

Use `ims.sql` and not `mysql-setup.sql`.

## Setup Instructions

1. Extract the submitted ZIP file.
2. Open the workspace folder `B19_24RP04887-ElieIradukunda_24RP00463-UfiteyesuEsther`.
3. Import the top-level `ims.sql` file into MySQL.
4. Open Eclipse.
5. Go to `File > Import > Maven > Existing Maven Projects`.
6. Select the `IMS` folder.
7. Wait for Maven dependencies to finish loading.
8. Make sure Java 21 is selected in Eclipse.
9. Open `src/main/java/com/airtel/inventory/ImsApplication.java`.
10. Right click `ImsApplication.java`.
11. Choose `Run As > Java Application`.
12. Open `http://localhost:8082/login`.

## How To Use The System

1. Log in using the SysAdmin credentials above.
2. Open `Dashboard` to see system summary.
3. Open `Asset Registry` to add, edit, delete, retire, search, and filter assets.
4. Open `Assignments` to issue and return items.
5. Open `Reports` to view and export reports.
6. Open `Audit Trail` to see system history.

## Notes

- The database name used by the system is `ims`.
- If port `8082` is already in use, change `server.port` in `src/main/resources/application.properties`.

CREATE DATABASE IF NOT EXISTS ims
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ims;

CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(160) NOT NULL,
    username VARCHAR(80) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_role VARCHAR(40) NOT NULL,
    enabled BIT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_app_user_username UNIQUE (username),
    CONSTRAINT uk_app_user_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS departments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(120) NOT NULL,
    location VARCHAR(120) NOT NULL,
    manager_name VARCHAR(120),
    PRIMARY KEY (id),
    CONSTRAINT uk_department_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_code VARCHAR(40) NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(160) NOT NULL,
    phone VARCHAR(40),
    job_title VARCHAR(120),
    department_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_employee_code UNIQUE (employee_code),
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES departments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS assets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    asset_tag VARCHAR(50) NOT NULL,
    serial_number VARCHAR(80) NOT NULL,
    asset_type VARCHAR(40) NOT NULL,
    brand VARCHAR(80) NOT NULL,
    model VARCHAR(120) NOT NULL,
    operating_system VARCHAR(120),
    processor VARCHAR(120),
    ram_gb INT,
    storage_gb INT,
    purchase_date DATE,
    warranty_expiry DATE,
    home_department_id BIGINT NOT NULL,
    assigned_department_id BIGINT,
    assigned_employee_id BIGINT,
    asset_status VARCHAR(40) NOT NULL,
    asset_condition VARCHAR(40) NOT NULL,
    notes VARCHAR(1500),
    last_movement_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_asset_tag UNIQUE (asset_tag),
    CONSTRAINT uk_asset_serial_number UNIQUE (serial_number),
    CONSTRAINT fk_asset_home_department FOREIGN KEY (home_department_id) REFERENCES departments (id),
    CONSTRAINT fk_asset_assigned_department FOREIGN KEY (assigned_department_id) REFERENCES departments (id),
    CONSTRAINT fk_asset_assigned_employee FOREIGN KEY (assigned_employee_id) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS asset_movements (
    id BIGINT NOT NULL AUTO_INCREMENT,
    asset_id BIGINT NOT NULL,
    movement_type VARCHAR(30) NOT NULL,
    from_department_id BIGINT,
    to_department_id BIGINT,
    employee_id BIGINT,
    issued_by VARCHAR(120) NOT NULL,
    returned_by VARCHAR(120),
    issued_at DATETIME(6) NOT NULL,
    returned_at DATETIME(6),
    condition_at_issue VARCHAR(40) NOT NULL,
    condition_at_return VARCHAR(40),
    issue_notes VARCHAR(1500),
    return_notes VARCHAR(1500),
    PRIMARY KEY (id),
    CONSTRAINT fk_asset_movement_asset FOREIGN KEY (asset_id) REFERENCES assets (id),
    CONSTRAINT fk_asset_movement_from_department FOREIGN KEY (from_department_id) REFERENCES departments (id),
    CONSTRAINT fk_asset_movement_to_department FOREIGN KEY (to_department_id) REFERENCES departments (id),
    CONSTRAINT fk_asset_movement_employee FOREIGN KEY (employee_id) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_log_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    asset_id BIGINT,
    asset_tag_snapshot VARCHAR(80) NOT NULL,
    audit_action VARCHAR(40) NOT NULL,
    actor_name VARCHAR(120) NOT NULL,
    details VARCHAR(1500) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_audit_log_asset FOREIGN KEY (asset_id) REFERENCES assets (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO app_users (full_name, username, email, password_hash, user_role, enabled, created_at)
VALUES (
    'SysAdmin',
    '24RP04887',
    '24rp04887@ims.local',
    '$2a$10$nRaAK4Gpen9DPQN.ZkXpMOv8CFDXyag2UBLB2om1mdBY104sWw2mK',
    'ADMIN',
    b'1',
    NOW()
)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    user_role = VALUES(user_role),
    enabled = VALUES(enabled);

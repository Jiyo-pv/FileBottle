-- ============================================
-- FileBottle Database Setup Script
-- ============================================

-- =========================
-- USERS TABLE
-- =========================
CREATE TABLE users (
    user_id NUMBER PRIMARY KEY,
    username VARCHAR2(100) UNIQUE NOT NULL,
    password VARCHAR2(255) NOT NULL,
    created_on DATE DEFAULT SYSDATE
);

CREATE SEQUENCE user_seq
START WITH 1
INCREMENT BY 1;


-- =========================
-- FILES TABLE
-- =========================
CREATE TABLE files (
    file_id NUMBER PRIMARY KEY,
    filename VARCHAR2(255) NOT NULL,
    filepath VARCHAR2(500) NOT NULL,
    filetype VARCHAR2(50),
    filesize NUMBER,
    created_on DATE DEFAULT SYSDATE,
    updated_on DATE,
    owner_id NUMBER,
    status VARCHAR2(20) DEFAULT 'ACTIVE',
    CONSTRAINT fk_files_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE SEQUENCE file_seq
START WITH 1
INCREMENT BY 1;


-- =========================
-- FILE SHARES TABLE
-- =========================
CREATE TABLE file_shares (
    share_id NUMBER PRIMARY KEY,
    file_id NUMBER,
    owner_id NUMBER,
    shared_with NUMBER,
    can_download NUMBER(1) DEFAULT 0,
    can_edit NUMBER(1) DEFAULT 0,
    shared_on DATE DEFAULT SYSDATE,

    CONSTRAINT fk_share_file
        FOREIGN KEY (file_id)
        REFERENCES files(file_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_share_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_share_user
        FOREIGN KEY (shared_with)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE SEQUENCE share_seq
START WITH 1
INCREMENT BY 1;


-- =========================
-- USER ACTIVITY LOG TABLE
-- =========================
CREATE TABLE activity_log (
    log_id NUMBER PRIMARY KEY,
    user_id NUMBER,
    action VARCHAR2(500),
    filename VARCHAR2(255),
    action_time DATE DEFAULT SYSDATE,

    CONSTRAINT fk_activity_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE SEQUENCE activity_seq
START WITH 1
INCREMENT BY 1;


-- =========================
-- SERVER LOG TABLE
-- =========================
CREATE TABLE server_log (
    log_id NUMBER PRIMARY KEY,
    action VARCHAR2(500),
    log_time DATE DEFAULT SYSDATE
);

CREATE SEQUENCE server_log_seq
START WITH 1
INCREMENT BY 1;

-- ============================================
-- END OF FILEBOTTLE DATABASE SETUP
-- ============================================
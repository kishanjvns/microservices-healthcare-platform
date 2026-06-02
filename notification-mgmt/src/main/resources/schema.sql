-- Notification Category Table
CREATE TABLE notification_category (
    id VARCHAR(36) PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification Template Table
CREATE TABLE notification_template (
    id VARCHAR(36) PRIMARY KEY,
    notification_category_id VARCHAR(36) NOT NULL,
    channel_type VARCHAR(20) NOT NULL,  -- EMAIL, SMS, POPUP
    template_content TEXT NOT NULL,
    subject VARCHAR(500),  -- For EMAIL only
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_category
        FOREIGN KEY (notification_category_id)
        REFERENCES notification_category(id),

    CONSTRAINT uq_category_channel
        UNIQUE (notification_category_id, channel_type)
);


CREATE TABLE IF NOT EXISTS notification_entity (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id UUID NOT NULL,
    channel_type VARCHAR(20) NOT NULL,
    recipient_address VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    rendered_content CLOB NOT NULL,
    content_type VARCHAR(50)
);


CREATE TABLE IF NOT EXISTS notification_delivery (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    notification_id UUID NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry INT NOT NULL DEFAULT 0,
    max_retries INT NOT NULL DEFAULT 3,
    vendor_status VARCHAR(100),
    vendor_response CLOB,
    error_message VARCHAR(1000),
    error_code VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraint
    CONSTRAINT fk_notification 
        FOREIGN KEY (notification_id) 
        REFERENCES notification_entity(id)
        ON DELETE CASCADE
);

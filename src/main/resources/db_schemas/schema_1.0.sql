--liquibase formatted sql



--changeset runOnChange Release10:1


CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` text DEFAULT NULL,
  `password` text DEFAULT NULL,
  `email` text DEFAULT NULL,
  `authentication` text DEFAULT NULL,
  `role`	ENUM('ROLE_ADMIN','ROLE_USER') DEFAULT NULL,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idxId (`id`),
  PRIMARY KEY (`id`)
);

CREATE TABLE `plugin`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`version` text DEFAULT NULL,
`description` text DEFAULT NULL,
`created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`last_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`path` text DEFAULT NULL,
PRIMARY KEY(`id`)
);

CREATE TABLE `model`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`plugin_id` int,
PRIMARY KEY(`id`),
FOREIGN KEY fk_plugin_id(`plugin_id`)
REFERENCES plugin(`id`)
);

CREATE TABLE `device`(
`id` int (11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`ip_address` text DEFAULT NULL,
`username`text DEFAULT NULL,
`password` text DEFAULT NULL,
`type` text DEFAULT NULL,
`importedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`importedStatus` int Default 1 NOT NULL,
`model_id` int,
PRIMARY KEY(`id`),
FOREIGN KEY fk_model_id(`model_id`)
   REFERENCES model(`id`)
);

CREATE TABLE `aci_project`
(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(255),
  `name`  varchar(255),
  `type` text,
  `customer_name` text,
  `sales_contact` text,
  `opportunity` text,
  `account` text,
  `logical_requirement` text,
  `logical_requirement_summary` text,
  `logical_result_summary` text,
  `created_by` text,
  `description` text,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deviceID` int,
  unique (userId, name),
  CONSTRAINT project_pkey PRIMARY KEY (`id`),
   FOREIGN KEY fk_deviceID(`deviceID`)
   REFERENCES device(`id`)
);

CREATE TABLE `project`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`description` text DEFAULT NULL,
`created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`last_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `logical_requirement_summary` text,
`userId` int,
INDEX idxId (`id`,`userId`),
PRIMARY KEY (`Id`),
FOREIGN KEY fk_userId(`userId`)
   REFERENCES user(`id`)

);


CREATE TABLE `audit_info`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`source_device` int ,
`importedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`auditedOn` timestamp NULL,
`auditStatus` text DEFAULT NULL,
PRIMARY KEY(`id`),
FOREIGN KEY fk_source_device(`source_device`)
REFERENCES device(`id`)
);

CREATE TABLE `contract`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`producerId` int DEFAULT NULL,
`consumerId` int DEFAULT NULL,
`auditId` int,
PRIMARY KEY(`id`),
FOREIGN KEY fk_auditId(`auditId`)
   REFERENCES audit_info(`id`)
    ON DELETE SET NULL
   ON UPDATE CASCADE
);

CREATE TABLE `subject`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`description` text DEFAULT NULL,
`contractId` int,
PRIMARY KEY(`id`),
FOREIGN KEY fk_contractId(`contractId`)
   REFERENCES contract(`id`)
    ON DELETE SET NULL
   ON UPDATE CASCADE
);

CREATE TABLE `filter`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`description` text DEFAULT NULL,
`subjectId` int,
`auditId` int,
PRIMARY KEY(`id`),
FOREIGN KEY fk_subjectId(`subjectId`)
   REFERENCES subject(`id`)
   ON DELETE SET NULL
   ON UPDATE CASCADE,
FOREIGN KEY fk_auditId(`auditId`)
   REFERENCES audit_info(`id`)
   ON DELETE SET NULL
   ON UPDATE CASCADE
);

CREATE TABLE `filter_entry`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` text DEFAULT NULL,
`etherType` text DEFAULT NULL,
`ipProtocol` text DEFAULT NULL,
`srcPort` text DEFAULT NULL,
`destPort` text DEFAULT NULL,
`filterId` int,
`auditId` int,
PRIMARY KEY(`id`),
FOREIGN KEY fk_filterId(`filterId`)
   REFERENCES filter(`id`)
    ON DELETE SET NULL
   ON UPDATE CASCADE,
 FOREIGN KEY fk_auditId(`auditId`)
   REFERENCES audit_info(`id`)
   ON DELETE SET NULL
   ON UPDATE CASCADE
);

CREATE VIEW repo_objects AS
    (SELECT 
        ae.id,
       'contract' AS `type`,
        ae.name,
        d.id AS `deviceId`,
        d.name AS 'source_device',
        au.importedOn,
        au.auditedOn,
       '1.0' AS 'objectVersion',
       '' AS 'auditStatus'
        
    FROM
        audit_info au
            INNER JOIN
        contract ae, device d where ae.auditId=au.id and d.id=au.source_device) UNION (SELECT 
        af.id,
       'filter' AS `type`,
        af.name,
        d.id AS `deviceId`,
        d.name AS 'source_device',
        au.importedOn,
        au.auditedOn,
       '1.0' AS 'objectVersion',
       '' AS 'auditStatus'
        
    FROM
        audit_info au
            INNER JOIN
        filter af, device d where af.auditId=au.id and d.id=au.source_device)  UNION (SELECT 
        fe.id,
        'filter entry' AS `type`,
        fe.name,
        d.id AS `deviceId`,
        d.name AS 'source_device',
        au.importedOn,
        au.auditedOn,
       '1.0' AS 'objectVersion',
       '' AS 'auditStatus'
        
    FROM
        audit_info au
            INNER JOIN
        filter_entry fe, device d where fe.auditId=au.id and  d.id=au.source_device);
        
INSERT INTO user (username,password,email,authentication,role) VALUES ('admin','admin','admin@maplelabs.com','Local Authentication','ROLE_ADMIN');
INSERT INTO user (username,password,email,authentication,role) VALUES ('user','user','user@maplelabs.com','Local Authentication','ROLE_USER');

INSERT INTO project(name,description) VALUES ('project1','');

INSERT INTO plugin(name,version,description,path) VALUES ('palo_plugin','1.0','Palo Alto Networks','/home/sizer/palo_plugin/');

INSERT INTO model(name,plugin_id) VALUES ('PA-300',1);
INSERT INTO model(name,plugin_id) VALUES ('PA-200',1);
INSERT INTO model(name) VALUES ('ACI');

INSERT INTO device (name,ip_address,username,password,importedStatus,model_id) VALUES ('PA-300','$$','uname','pwd',1,1);
INSERT INTO device(name,ip_address,username,password,type,importedStatus,model_id) VALUES ('APIC','1.2.3.4','admin','password','ACI',1,3);

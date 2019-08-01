-- -----------------------------------------------------
-- Table `account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
  `id`       int(11)      NOT NULL AUTO_INCREMENT,
  `username` varchar(50)  NOT NULL,
  `password` varchar(100) NOT NULL,
  `personId` varchar(50)  NOT NULL,
  `extra`    varchar(100) NOT NULL,
  `createAt` datetime     NOT NULL,
  `updateAt` datetime     NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_unique` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- -----------------------------------------------------
-- Table `loginRecord`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `loginRecord`;

CREATE TABLE IF NOT EXISTS `loginRecord`
(
  `id`       INT         NOT NULL AUTO_INCREMENT,
  `personId` INT         NOT NULL,
  `ip`       VARCHAR(50) NOT NULL,
  `device`   VARCHAR(50),
  `updateAt` datetime    NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `forget`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `forget`;

CREATE TABLE IF NOT EXISTS `forget`
(
  `id`        INT         NOT NULL AUTO_INCREMENT,
  `accountId` INT         NOT NULL,
  `code`      VARCHAR(50) NOT NULL,
  `createAt`  datetime    NOT NULL,
  `updateAt`  datetime    NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB;


BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `Author` (
	`id`	INTEGER,
	`name`	TEXT,
	`surname`	TEXT,
	`title`	TEXT,
	`university`	TEXT,
	PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `ResearchPapers` (
	`id`	INTEGER,
	`name`	TEXT,
	`subject`	TEXT,
	`keywords` TEXT,
	`text`  TEXT,
	PRIMARY KEY(`id`)
);
CREATE TABLE IF NOT EXISTS `ResearchPapers_Authors` (
    `researchP_id` INTEGER,
    `author_id` INTEGER,
    PRIMARY KEY (`researchP_id`, `author_id`),
    FOREIGN KEY(`researchP_id`) REFERENCES `ResearchPapers`(`id`),
    FOREIGN KEY(`author_id`) REFERENCES `Author`(`id`)
);
COMMIT;

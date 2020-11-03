/*
Script to help cleanup the abandoned rows in the searlized_script_step table
Recommed running one line at a time, and validating the output
*/

SELECT count(*) FROM tank.serialized_script_step;
SELECT count(*) FROM tank.script;
SELECT `AUTO_INCREMENT` FROM  INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'tank' AND TABLE_NAME = 'serialized_script_step';

CREATE TABLE IF NOT EXISTS tank.serialized_script_step_temp
(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `serialized_data` longblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=<update with existing count> DEFAULT CHARSET=utf8
SELECT steps.*
FROM tank.serialized_script_step AS steps
JOIN tank.script AS script
ON script.serial_step_id=steps.id;

SELECT count(*) FROM tank.serialized_script_step_temp;

ALTER TABLE tank.script DROP FOREIGN KEY FKC9E5D0CBD63BDD22;

RENAME TABLE tank.serialized_script_step TO tank.serialized_script_step_old;
RENAME TABLE tank.serialized_script_step_temp TO tank.serialized_script_step;

ALTER TABLE tank.script ADD CONSTRAINT `FKC9E5D0CBD63BDD22` FOREIGN KEY (`serial_step_id`) REFERENCES `serialized_script_step` (`id`);


/*
Validate scripts in Tank before performing the DROP
*/
DROP TABLE tank.serialized_script_step_old;

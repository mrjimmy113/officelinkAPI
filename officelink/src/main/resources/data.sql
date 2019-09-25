--insert into type_question value(1,"SINGLE");
--insert into type_question value(2,"MULTIPLE");
insert into type_question value(NULL,"TEXT");
insert into type_question value(NULL,"RATE");
insert into type_question value(NULL,"VAS");

INSERT INTO role VALUES (1, "employer"), (2, "employee"), (3, "system_admin");

INSERT INTO workplace VALUES (1,NULL,NULL,'\0',"Office Link System Dummie");

/*Pass System admin 123456*/
INSERT INTO account VALUES (1,NULL,NULL,"officelink@gmail.com","Office Link System Dummie",1,0,"","$2a$10$tiD920g0y5mgxnG1/QPvaO/63Ct1HotA/4Lq1dzIxi1zXR5y4yzfu",NULL,3,1);

INSERT INTO configuration VALUES (1,NULL,NULL,0, b'1','\0',"0 5 0 * * *",NULL,1);

INSERT INTO `word_cloud_filter` VALUES ('1',NULL,NULL, 0, 1,0, 'DEFAULT', 1);

INSERT INTO `word` VALUES (35,'would'),(34,'want'),(33,'not'),(32,'the'),(31,'should'),(30,'our'),(29,'their'),(28,'your'),(27,'yours'),(26,'her'),(25,'his'),(24,'my'),(23,'did'),(22,'had'),(21,'were'),(20,'was'),(19,'does'),(18,'do'),(17,'has'),(16,'have'),(15,'are'),(14,'is'),(13,'it'),(12,'she'),(11,'he'),(10,'those'),(9,'these'),(8,'that'),(7,'this'),(6,'they'),(5,'we'),(4,'you'),(3,'i'),(2,'an'),(1,'a');

INSERT INTO `filter_word` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),(1,35);

INSERT INTO `category` VALUES (NULL,"Level of Autonomy, Mastery, Purpose","Personal growth"), (NULL, "Stress and Personal Health", "Wellness"),(NULL,"Collaboration, Trust, Communication with Manager","Relationship with manager"),(NULL,"Collaboration, Trust, Communication with Co-worker","Relationship with Co-worker"),(NULL,"Compenstation, Role with orgranization, Workplace","Satisfaction"),(NULL,"Happy at work, Work-life balance","Happiness")
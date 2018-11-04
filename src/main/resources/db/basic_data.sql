/** CREATE DEFAULT ROLES **/
INSERT INTO role(id,name) VALUES (0,'REGISTERED_USER');
INSERT INTO role(id,name) VALUES (1,'BOOKING_MANAGER');

/** CREATE DEFAULT USERS **/
INSERT INTO user(id,name, email, birthday, password) VALUES (0,'admin','admin@gmail.com',date'1978-12-08','$2a$10$Cm4XdkWJV2ab4KHotJEfHOuJ4OeNHSiHPrgs/9k6RFAJlod94r7ae');
INSERT INTO user(id,name, email, birthday, password) VALUES (1,'user','oleg.motorin@gmail.com',date'1978-12-08','$2a$10$aluAP7/bZo7uzm2yiG6k1eSd6KBk2ZFhKWvhclV1ONGSUhy3zzy6i');

/** ASSIGN ROLES FOR USERS **/
INSERT INTO roles(user_id, role_id) VALUES (0,0);
INSERT INTO roles(user_id, role_id) VALUES (0,1);
INSERT INTO roles(user_id, role_id) VALUES (1,0);

/** CREATE ACCOUNT DATA FOR USERS **/
INSERT INTO user_account(user_id, cash) VALUES (0, 9999);
INSERT INTO user_account(user_id, cash) VALUES (1, 1000);
'use strict';

var executeSqlQuery = require('../index').executeSqlQuery;
const crypto = require('crypto');

/**
 * Add a new user to Perses
 * Add a new user to Perses
 *
 * body User Add a new user to Perses
 * user_name String Name of the user to create
 * returns User
 **/
exports.addUser = function (body, user_name) {
  return new Promise(function (resolve, reject) {

    const hash_api_key = crypto.createHash('sha256');
    const hash_password = crypto.createHash('sha256');

    const api_key = genAPIKey()
    hash_api_key.update(api_key);
    const api_key_hashed = hash_api_key.digest('hex');

    hash_password.update(body.password);
    const password_hashed = hash_password.digest('hex');
    
    var sqlQuery = "INSERT INTO users (name, last_name_1, last_name_2, nid, birth_date, user_name, email, password, api_key, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    var params = [body.name, body.last_name_1, body.last_name_2, body.nid, body.birth_date, body.user_name, body.email, password_hashed, api_key_hashed, body.role];
    
    console.log(`API key for user '${body.user_name}' is ${api_key}`);

    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });

  });
}


/**
 * Deletes an existing user from the database
 * Deletes an existing user from the database
 *
 * user_name String Name of user to delete
 * returns User
 **/
exports.deleteUser = function (user_name) {
  return new Promise(function (resolve, reject) {

    var sqlQuery = "DELETE FROM users WHERE user_name=(?)";
    var params = [user_name];

    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });

  });
}


/**
 * Get the data from all the users
 * Get the data from all the users
 *
 * returns List
 **/
exports.getAllUsers = function () {
  return new Promise(function (resolve, reject) {

    var sqlQuery = `SELECT * FROM users;`;
    var params = [];
    
    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });

  });
}


/**
 * Get the data from the user by its name
 * Returns all the info from the required user
 *
 * user_name String The username from the user
 * returns User
 **/
exports.getUserByUsername = function (user_name) {
  return new Promise(function (resolve, reject) {

    var sqlQuery = "SELECT * FROM users WHERE user_name=(?)";
    var params = [user_name];


    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });

  });
}


/**
 * Updates an user by its name
 * Updates an user by its name
 *
 * body User Updates an user by its name
 * user_name String The username from the user
 * returns User
 **/
exports.updateUserByUsername = function (body, user_name) {
  return new Promise(function (resolve, reject) {

    //var sqlQuery = `UPDATE users SET name='${body.name}', last_name_1='${body.last_name_1}', last_name_2='${body.last_name_2}', nid='${body.nid}', birth_date='${body.birth_date}', user_name='${body.user_name}', email='${body.email}', password='${body.password}' WHERE user_name='${user_name}';`;
    var sqlQuery = "UPDATE users SET name=(?), last_name_1=(?), last_name_2=(?), nid=(?), birth_date=(?), user_name=(?), email=(?), password=(?) WHERE user_name=(?)";
    var params = [body.name, body.last_name_1, body.last_name_2, body.nid, body.birth_date, body.user_name, body.email, body.password, user_name];
    
    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });

  });
}


function genAPIKey() {
  const characters = 'abcdefghijklmnopqrstuvwxyz0123456789';
  const charactersLength = characters.length;
  let result = '';

  for (let i = 0; i < 30; i++) {
    const randomIndex = Math.floor(Math.random() * charactersLength);
    result += characters.charAt(randomIndex);
  }

  return result;
}


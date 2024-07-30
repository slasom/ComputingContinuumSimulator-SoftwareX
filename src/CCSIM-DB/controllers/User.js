'use strict';

var utils = require('../utils/writer.js');
var User = require('../service/UserService');

module.exports.addUser = function addUser (req, res, next, body, user_name) {
  User.addUser(body, user_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.deleteUser = function deleteUser (req, res, next, user_name) {
  User.deleteUser(user_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getAllUsers = function getAllUsers (req, res, next) {
  User.getAllUsers()
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUserByUsername = function getUserByUsername (req, res, next, user_name) {
  User.getUserByUsername(user_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.updateUserByUsername = function updateUserByUsername (req, res, next, body, user_name) {
  User.updateUserByUsername(body, user_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

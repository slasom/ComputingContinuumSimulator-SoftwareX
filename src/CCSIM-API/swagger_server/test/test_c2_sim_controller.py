# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.test import BaseTestCase


class TestCCSIMController(BaseTestCase):
    """CCSIMController integration test stubs"""

    def test_get_all_executions_from_database(self):
        """Test case for get_all_executions_from_database

        Retrieves information about all executions.
        """
        response = self.client.open(
            '//database/executions',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_all_executions_from_user_from_database(self):
        """Test case for get_all_executions_from_user_from_database

        Retrieves all executions from a certain user.
        """
        response = self.client.open(
            '//database/executions/{user_name}'.format(user_name='user_name_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_all_projects_from_database(self):
        """Test case for get_all_projects_from_database

        Retrieves information about all projects.
        """
        response = self.client.open(
            '//database/projects',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_all_projects_from_user_from_database(self):
        """Test case for get_all_projects_from_user_from_database

        Retrieves all projects from a certain user.
        """
        response = self.client.open(
            '//database/projects/{user_name}'.format(user_name='user_name_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_all_users_from_database(self):
        """Test case for get_all_users_from_database

        Retrieves information about all users.
        """
        response = self.client.open(
            '//database/users',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_execution_from_database(self):
        """Test case for get_execution_from_database

        Fecthes information about a specific execution.
        """
        response = self.client.open(
            '//database/execution/{execution_name}'.format(execution_name='execution_name_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_project_from_database(self):
        """Test case for get_project_from_database

        Fecthes information about a specific project.
        """
        response = self.client.open(
            '//database/project/{project_name}'.format(project_name='project_name_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_user_from_database(self):
        """Test case for get_user_from_database

        Fecthes information about a specific user.
        """
        response = self.client.open(
            '//database/user/{user_name}'.format(user_name='user_name_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()

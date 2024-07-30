# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.test import BaseTestCase


class TestDownloadController(BaseTestCase):
    """DownloadController integration test stubs"""

    def test_download_files_from_execution(self):
        """Test case for download_files_from_execution

        Download all the files related to a Perses Execution.
        """
        response = self.client.open(
            '//download/{execution_name}'.format(execution_name='execution_name_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()

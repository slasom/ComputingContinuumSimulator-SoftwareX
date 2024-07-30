# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class Payment(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """
    def __init__(self, payment_id: int=None, user_name: str=None, project_name: str=None, execution_name: str=None, associated_cost: float=None):  # noqa: E501
        """Payment - a model defined in Swagger

        :param payment_id: The payment_id of this Payment.  # noqa: E501
        :type payment_id: int
        :param user_name: The user_name of this Payment.  # noqa: E501
        :type user_name: str
        :param project_name: The project_name of this Payment.  # noqa: E501
        :type project_name: str
        :param execution_name: The execution_name of this Payment.  # noqa: E501
        :type execution_name: str
        :param associated_cost: The associated_cost of this Payment.  # noqa: E501
        :type associated_cost: float
        """
        self.swagger_types = {
            'payment_id': int,
            'user_name': str,
            'project_name': str,
            'execution_name': str,
            'associated_cost': float
        }

        self.attribute_map = {
            'payment_id': 'payment_id',
            'user_name': 'user_name',
            'project_name': 'project_name',
            'execution_name': 'execution_name',
            'associated_cost': 'associated_cost'
        }
        self._payment_id = payment_id
        self._user_name = user_name
        self._project_name = project_name
        self._execution_name = execution_name
        self._associated_cost = associated_cost

    @classmethod
    def from_dict(cls, dikt) -> 'Payment':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The Payment of this Payment.  # noqa: E501
        :rtype: Payment
        """
        return util.deserialize_model(dikt, cls)

    @property
    def payment_id(self) -> int:
        """Gets the payment_id of this Payment.


        :return: The payment_id of this Payment.
        :rtype: int
        """
        return self._payment_id

    @payment_id.setter
    def payment_id(self, payment_id: int):
        """Sets the payment_id of this Payment.


        :param payment_id: The payment_id of this Payment.
        :type payment_id: int
        """

        self._payment_id = payment_id

    @property
    def user_name(self) -> str:
        """Gets the user_name of this Payment.


        :return: The user_name of this Payment.
        :rtype: str
        """
        return self._user_name

    @user_name.setter
    def user_name(self, user_name: str):
        """Sets the user_name of this Payment.


        :param user_name: The user_name of this Payment.
        :type user_name: str
        """

        self._user_name = user_name

    @property
    def project_name(self) -> str:
        """Gets the project_name of this Payment.


        :return: The project_name of this Payment.
        :rtype: str
        """
        return self._project_name

    @project_name.setter
    def project_name(self, project_name: str):
        """Sets the project_name of this Payment.


        :param project_name: The project_name of this Payment.
        :type project_name: str
        """

        self._project_name = project_name

    @property
    def execution_name(self) -> str:
        """Gets the execution_name of this Payment.


        :return: The execution_name of this Payment.
        :rtype: str
        """
        return self._execution_name

    @execution_name.setter
    def execution_name(self, execution_name: str):
        """Sets the execution_name of this Payment.


        :param execution_name: The execution_name of this Payment.
        :type execution_name: str
        """

        self._execution_name = execution_name

    @property
    def associated_cost(self) -> float:
        """Gets the associated_cost of this Payment.


        :return: The associated_cost of this Payment.
        :rtype: float
        """
        return self._associated_cost

    @associated_cost.setter
    def associated_cost(self, associated_cost: float):
        """Sets the associated_cost of this Payment.


        :param associated_cost: The associated_cost of this Payment.
        :type associated_cost: float
        """

        self._associated_cost = associated_cost
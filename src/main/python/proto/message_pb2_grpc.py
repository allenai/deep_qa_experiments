import grpc
from grpc.framework.common import cardinality
from grpc.framework.interfaces.face import utilities as face_utilities

import message_pb2 as message__pb2
import message_pb2 as message__pb2


class SolverServiceStub(object):
  """The service definition
  """

  def __init__(self, channel):
    """Constructor.

    Args:
      channel: A grpc.Channel.
    """
    self.AnswerQuestion = channel.unary_unary(
        '/deep_qa.SolverService/AnswerQuestion',
        request_serializer=message__pb2.QuestionRequest.SerializeToString,
        response_deserializer=message__pb2.QuestionResponse.FromString,
        )


class SolverServiceServicer(object):
  """The service definition
  """

  def AnswerQuestion(self, request, context):
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')


def add_SolverServiceServicer_to_server(servicer, server):
  rpc_method_handlers = {
      'AnswerQuestion': grpc.unary_unary_rpc_method_handler(
          servicer.AnswerQuestion,
          request_deserializer=message__pb2.QuestionRequest.FromString,
          response_serializer=message__pb2.QuestionResponse.SerializeToString,
      ),
  }
  generic_handler = grpc.method_handlers_generic_handler(
      'deep_qa.SolverService', rpc_method_handlers)
  server.add_generic_rpc_handlers((generic_handler,))

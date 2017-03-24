FROM python:3.5

RUN apt-get update && apt-get install -y gfortran liblapack-dev libblas-dev
RUN wget https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -O miniconda.sh && \
  bash miniconda.sh -b -p $HOME/miniconda

COPY ./deep_qa /deep_qa
COPY ./deep_qa/requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY ./src /src

ENV PATH $HOME/miniconda/bin:$PATH
ENV PYTHONHASHSEED 2157
ENV PYTHONPATH /deep_qa:$PYTHONPATH

EXPOSE 50051

COPY ./bidaf-tf-moretrained /tmp/models/bidaf-tf-moretrained/bidaf

ENTRYPOINT ["python", "src/main/python/server.py", "50051"]

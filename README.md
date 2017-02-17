[![Build Status](https://semaphoreci.com/api/v1/allenai/deep_qa_experiments/branches/master/shields_badge.svg)](https://semaphoreci.com/allenai/deep_qa_experiments)

# Deep QA Experiments

This repository contains scala code for setting up and running experiments with
[DeepQA](https://github.com/allenai/deep_qa).  The main point here is to have reasonable pipeline
management for setting up data and running comparison experiments between several models on some
dataset.

# Usage Guide

To use this code, you set up an experiment in scala, then run it using `sbt`.  Some documentation
on how to do this is found in the [README for the `org.allenai.deep_qa.experiments`
package](src/main/scala/org/allenai/deep_qa/experiments/).

# Contributing

If you use this code and think something could be improved, pull requests are very welcome.
Opening an issue is ok, too, but we're a lot more likely to respond to a PR.  The primary
maintainer of this code is [Matt Gardner](https://matt-gardner.github.io/).

# License

This code is released under the terms of the [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.en.html).

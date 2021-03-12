# ASAP
Accurate Spot Analysis and Prediction (ASAP) Framework

The ASAP Framework combines short term price prediction with long term trend analysis for using AWS/EC2 spot instances. It provides a utility-based mechanism from the cloud user perspective, that balances instance cost and availability for the short term price prediction. It also provides a long term price trend analysis of price variation, using flexible LSTM neural networks architecture and implementation.

## Projects

This repository has the following projects:

* [lstm-analysis](https://github.com/gjportella/asap/tree/main/lstm-analysis): combined LSTM-based (neural network) and utility-based analysis. The experimental results are detailed in [4].
* [statistical-analysis](https://github.com/gjportella/asap/tree/main/statistical-analysis): statistical analysis and experimental results detailed in WSCAD 2016 [1] and CCPE 2017 [2].
* [utility-analysis](https://github.com/gjportella/asap/tree/main/utility-analysis): utility-based analysis and experimental results detailed in IEEE-Cloud 2019 [3] and [4].

Please see further details in the README file in each project.

## References

\[1\] Portella, Gustavo, Genaina N. Rodrigues and Alba C.M.A. Melo. Análise de precificação de recursos utilizados em computação em nuvem. XVII Simpósio de Sistemas Computacionais de Alto Desempenho (WSCAD), Aracaju, SE, BRA, 2016. SBC. http://www.lbd.dcc.ufmg.br/colecoes/wscad/2016/017.pdf.

\[2\] Portella, Gustavo, Genaina N. Rodrigues, Eduardo Nakano and Alba C.M.A. Melo. Statistical analysis of amazon ec2 cloud pricing models. Concurrency and Computation: Practice and Experience, 30(7):1–15, 2017. https://doi.org/10.1002/cpe.4451.

\[3\] Portella, Gustavo, Genaina N. Rodrigues, Eduardo Nakano and Alba C.M.A. Melo. Utility-based strategy for balanced cost and availability at the cloud spot market. 2019 IEEE 12th International Conference on Cloud Computing (CLOUD). 214–218, 2019. https://doi.org/10.1109/CLOUD.2019.00045.

\[4\] Portella, Gustavo, Genaina N. Rodrigues, Eduardo Nakano, Azzedine Boukerche and Alba C.M.A. Melo. A statistical and neural network combined approach for the
cloud spot market. Submitted to a journal in October 2020.

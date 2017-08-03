# LexiExp -- Free open source sentiment lexicon expansion script

Please read the following <a href="https://github.com/uhh-lt/LexiExp/blob/master/LICENSE.txt"> License </a> agreement. LexiExp is licensed under ASL 2.0 and other lenient licenses, allowing its use for academic and commercial purposes without restrictions. The licenses of its compenents are mixed licensed and are individually listed in Data/Licenses.

LexiExp is a tool for expanding existing sentiment seed lexicon. LexiExp provides a polarity estimation for the new expanded lexicon using a statistical cooccurance calculation. 
## How to Use LexiExp from Command Line
1. <b>Download:</b> Source code is avilable under <a href="https://github.com/uhh-lt/LexiExp">source code</a> or simply use the excutable <a href="https://github.com/uhh-lt/LexiExp/blob/master/LexiExp0.0.1.jar">jar file</a>.
2. <b>Command Line & Input Parameters</b>: 

    $ java -jar LexiExp0.0.1.jar -s \<string\> [-e \<int\>] [-db \<string\>] -o \<string\>

      -h                     help

      -db,--database <arg>   Database/model name (DEFAULT: reviewsTrigram). This database works for English lexicon.
            List of available languages and their corresponding models:
            
    * <b>English:</b> reviewsTrigram , wikipediaTrigram , twitter2012Bigram , trigram
    * <b>German:</b> germanTrigram , twitterDETrigram
    * <b>Dutch:</b> dutchTrigram
    * <b>French:</b> frenchTrigram
    * <b>Spanish:</b> spanishTrigram
    * <b>Bengali:</b> bengaliBigram
    * <b>Indian:</b> hindiBigram , hindiTrigram
    * <b>Arabic:</b> arabicTrigram
    * <b>Turkish:</b> turkishTrigram
    * <b>Hebrew:</b> hebrewTrigram
    * <b>Russian:</b> russianTrigram
    
     -e,--expansion <arg>   Number of expansions (DEFAULT: 10)

     -o,--output <arg>      Output file name (DEFAULT: out_expanded_lexicon.txt)

     -s,--seed <arg>        Seed set input file word"\TAB"polarity pairs
                              [w_1\tp_1]
                              [w_2\tp_2]
                              ...
                              [w_m\tp_m]
                              (DEFAULT: <a href="https://github.com/uhh-lt/LexiExp/blob/master/lexicon"> lexicon </a>)

    To run the example, please add the following <a href="https://github.com/uhh-lt/LexiExp/blob/master/lexicon"> english lexicon sample</a> file in the same directory and run the jar without any parameters:

    $ java -jar LexiExp0.0.1.jar

## Resources:
1. Kumar, A., Kohail, S., Kumar, A., Ekbal, A., Biemann, C. (2016): IIT-TUDA at SemEval-2016 Task 5: Beyond Sentiment Lexicon: Combining Domain Dependency and Distributional Semantics Features for Aspect Based Sentiment Analysis, In Proceedings of the 10th International Workshop on Semantic Evaluation, San Diego, CA, USA. (selected for best of SemEval session) (<a href="https://www.inf.uni-hamburg.de/en/inst/ab/lt/publications/2016-kumar-etal-absa-semeval.pdf">pdf</a>)
2. Kumar, A., Kohail, S., Ekbal, A., Biemann C. (2015): IIT-TUDA: System for Sentiment Analysis in Indian Languages using Lexical Acquisition. In: Third International Conference on Mining Intelligence and Knowledge Exploration (MIKE 2015). Hyderabad, India (<a href="https://www.lt.informatik.tu-darmstadt.de/fileadmin/user_upload/Group_LangTech/publications/KumarEtAl_MIKE2015.pdf">pdf</a>)
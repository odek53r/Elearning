Elearning
=========

一個可以自動根據英文文章上下文產生問題的系統

*** stanford NLP library已更新

stanford NLP library links = http://nlp.stanford.edu/software/stanford-corenlp-full-2015-01-29.zip

stanford-corenlp-3.5.1.jar,stanford-corenlp-3.5.1-javadoc.jar,stanford-corenlp-3.5.1-models.jar,stanford-corenlp-3.5.1-sources.jar

在stanford NLP library links下載完後，將上面4個.jar檔放入lib資料夾裡

將files.txt打開，依照裡面格式將所有文本資料路徑以","隔開寫入，不保留空白

在command line裡打上 java -jar NLP.jar ./files.txt，所有文本會自動產生問句，各別文本所產生的問句會以.txt檔存在相同路徑裡

* 文本裡的每個句子，務必確保都有以空白隔開，否則有可能不會產生問句
* java版本使用java 8以上
* lib、resource資料夾勿任意改名或更動



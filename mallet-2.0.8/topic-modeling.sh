#! /bin/bash

input_source="$1"
suffix="$2"

echo "Input source: $input_source"
echo "The number of topics: $suffix"

# Please change "JSEA-path" to your real path of JSEA
root_dir="/Users/wangtianxia1/IdeaProjects/JSEA/JSEA/JSEA-store-data/"
input_dir_1="preprocessOutput/PreProcessTool"
input_dir_2="preprocess"
output_dir_1="outputMallet"
output_dir_2="showFile"

output_file="$input_source-$suffix";

# Check the existence of input and output folder
[ ! -d "$root_dir/$input_dir_1" ] && echo "There is no $input_dir_1"
[ ! -d "$root_dir/$input_dir_1/$input_dir_2" ] && echo "There is no $input_dir_2"
[ ! -d "$root_dir/$output_dir_1" ] && mkdir "$output_dir_1"
[ ! -d "$root_dir/$output_dir_2" ] && mkdir -p "$root_dir/$output_dir_2"

# Copy documentsWordsCount.txt to output_dir_2
cp -R ${root_dir}/${input_dir_1}/documentsWordsCount.txt ${root_dir}/${output_dir_2}/documentsWordsCount.txt

echo "Star importing..."
# Import documents and remove common English stopwords
/bin/sh bin/mallet import-dir --input ${root_dir}/${input_dir_1}/${input_dir_2} --output ${root_dir}/${output_dir_1}/${output_file}.mallet --keep-sequence --remove-stopwords

echo "Start tranning..."
# Train topics. We recommend the following parameter settings of Topic Modeling, and you can also change them.
/bin/sh bin/mallet train-topics --input ${root_dir}/${output_dir_1}//${output_file}.mallet --num-topics ${suffix} --num-top-words 10 --num-iterations 1000 --optimize-interval 10 --output-state ${root_dir}/${output_dir_2}/topic-state.gz --output-topic-keys ${root_dir}/${output_dir_2}/keys.txt --output-doc-topics ${root_dir}/${output_dir_2}/composition.txt --xml-topic-phrase-report ${root_dir}/${output_dir_2}/topic-phrases.xml --word-topic-counts-file ${root_dir}/${output_dir_2}/word-top.txt --output-topic-docs ${root_dir}/${output_dir_2}/topic-docs.txt

exit 0

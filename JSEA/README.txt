1. Please configure following “Configuration.txt”;
2. Run JSEA;
3. Upload the directory of source code that need to process.
4. Select preprocessing steps as your demands, and we recommend do all steps;
5. Click “Start Preprocessing”;
6. Open bash and enter “MALLET” directory;
7. Run “mallet-2.0.8/topic-modeling.sh”: 
bash topic-modeling.sh -input_file -topic_number;
#-input_file: the project name, like JHotDraw;
#-topic_number: the number of topics
8. You can use the system via “show” page (The project overview page) and “search” page (JSEA-Search).
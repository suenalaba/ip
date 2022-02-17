package duke.database;

import duke.entity.Task;
import duke.entity.Todo;
import duke.entity.Deadline;
import duke.entity.Event;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class TaskDatabase implements Database {

    private static final String SEPARATOR = "|";
    private static TaskDatabase instance = null;

    public static TaskDatabase getInstance() {
        if (instance == null) {
            instance = new TaskDatabase();
        }
        return instance;
    }

    @Override
    public ArrayList<Task> read(String filePath) {

        ArrayList<String> stringTokens = FileRead.readFileContentByLine(filePath);
        ArrayList<Task> taskList = new ArrayList<>();
        try {
            for (String stringToken : stringTokens) {

                // get individual 'fields' of the string separated by SEPARATOR
                StringTokenizer st = new StringTokenizer(stringToken, SEPARATOR); // pass in the string to the string tokenizer
                // using delimiter ","

                String taskType = st.nextToken().trim();
                String taskStatus = st.nextToken().trim();
                String taskDesc = st.nextToken().trim();
                String date = st.nextToken().trim();
                boolean isTaskDone = Boolean.parseBoolean(taskStatus);
                switch (taskType) {
                case "D":
                    Deadline deadline = new Deadline(taskDesc, isTaskDone, date);
                    taskList.add(deadline);
                    break;
                case "E":
                    Event event = new Event(taskDesc, isTaskDone, date);
                    taskList.add(event);
                    break;
                case "T":
                default:
                    Todo todo = new Todo(taskDesc, isTaskDone);
                    taskList.add(todo);
                    break;
                }
                System.out.println("Your data has been loaded successfully");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Your text file is in the appropriate format, hence no data was loaded.");
        }
        return taskList;
    }
    @Override
    public void save(String filepath, ArrayList tasks) throws IOException  {
        ArrayList<String> taskInStrings = new ArrayList<>();
        for (Object task : tasks) {
            String[] tokenArray = stringToToken(task.getClass().toString());
            String taskType = tokenArray[tokenArray.length - 1];
            StringBuilder sb = new StringBuilder();
            switch (taskType) {
            case "Event":
                Event event = (Event) task;
                String taskStatus = Boolean.toString(event.getIsTaskDone());
                String taskDesc = event.getDescription();
                String taskDate = event.getDuration();
                sb.append("E");
                sb.append(SEPARATOR);
                sb.append(taskStatus);
                sb.append(SEPARATOR);
                sb.append(taskDesc);
                sb.append(SEPARATOR);
                sb.append(taskDate);
                taskInStrings.add(sb.toString());
                break;
            case "Deadline":
                Deadline deadline = (Deadline) task;
                taskStatus = Boolean.toString(deadline.getIsTaskDone());
                taskDesc = deadline.getDescription();
                taskDate = deadline.getBy();
                sb.append("D");
                sb.append(SEPARATOR);
                sb.append(taskStatus);
                sb.append(SEPARATOR);
                sb.append(taskDesc);
                sb.append(SEPARATOR);
                sb.append(taskDate);
                taskInStrings.add(sb.toString());
                break;
            case "Todo":
            default:
                Todo todo = (Todo) task;
                taskStatus = Boolean.toString(todo.getIsTaskDone());
                taskDesc = todo.getDescription();
                taskDate = "nodate";
                sb.append("T");
                sb.append(SEPARATOR);
                sb.append(taskStatus);
                sb.append(SEPARATOR);
                sb.append(taskDesc);
                sb.append(SEPARATOR);
                sb.append(taskDate);
                taskInStrings.add(sb.toString());
                break;
            }
        }
        FileWrite.writeToFile(filepath, taskInStrings);
    }

    private static String[] stringToToken(String userInput) {
        return userInput.split("\\.");
    }
}
# BLT

[Report](./blt-report.pdf)

[Video](INSERT LINK HERE SIMILAR TO REPORT)

## Team Members:

| Name            | Student Number | Email                         |
|-----------------|----------------|-------------------------------|
| Brendan Sadlier | 20402884       | brendan.sadlier@ucdconnect.ie |
| Liam Smyth      | 20321311       | liam.smyth@ucdconnect.ie      |
| Thomas Coogan   | 20356556       | thomas.coogan@ucdconnect.ie   |

## Summary:

The application takes in a list of horses from a database and generates races using 
these horses. The odds of each horse is dynamically calculated based on the score of 
each horse in regard to the scores of the other horses in the same race. 
These races are then sent to a bookie who shows the odds and the races to the client. 
The client can bet on any horse in the race they are shown. 

The client can choose what horse to bet on and how much to bet on the horse, 
or they can choose not to bet on the current race at all.  
Races are run every minute, and the winner of the race is 
broadcast to every client that bet on that race. 
A new race is then generated and broadcast to all connected clients and the 
process repeats.


## How to Run the Code:

1. Ensure you have Minikube and Docker installed on your machine.
2. Start Docker desktop
3. You will need 2 terminals open for this project.
4. In the first terminal, run the following commands:
    ```bash
    minikube start
    kubectl create configmap mongo-init-configmap --from-file=mongo-init.js
    ```

    Wait until all the pods are running (this might take a while), you can see the status of the pods using:

    ```bash
    kubectl get pods
    ```

    When the pods are running:

    ```bash
    kubectl expose deployment bookie --type=LoadBalancer --port=8080
    kubectl tunnel
    ```

5. In the second terminal run:
    ```bash
    mvn compile exec:java -pl client
    ```
The client is now running and you can interact with the system.

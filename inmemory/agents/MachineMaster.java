package inmemory.agents;

// java -cp lib\jade.jar;out\production\agents\ jade.Boot -gui -agents s1:inmemory.agents.MachineMaster;w1:inmemory.agents.Worker;w2:inmemory.agents.Worker;w3:inmemory.agents.Worker
// java -cp lib\jade.jar;out\production\agents\ jade.Boot -container -host 192.168.0.80 -agents w4:inmemory.agents.Worker

import inmemory.DataContainer;
import inmemory.textProcessing.TextJobPart;
import inmemory.textProcessing.TextJobProcessor;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.ControllerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.System.*;
import static java.lang.Thread.sleep;

/**
 * Created by Grzegorz on 2017-01-23.
 */

public class MachineMaster extends Agent {
    private MachineMasterGUI myGui;
    private AID[] workersOnPlatform;
    private ArrayList<AID> workersOnMachine;
    private ArrayList<AID> workersOutsideMachine;
    private ArrayList<TextJobPart> partsToProcess = null;
    private ArrayList<TextJobPart> partsProcessed = null;
    private boolean jobProcessing = false;
    private OutputGUI resultsGui;
    private Random random = new Random();
    private boolean inmmemory;
    private StringBuilder fullText;
    private long timestamp;

    protected void setup() {
        myGui = new MachineMasterGUI(this);
        myGui.showGui();
        out.println("MachineMaster "+getAID().getName()+" HERE I AM.");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("text-jobs");
        sd.setName("text-jobs-master");

        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new refreshWorkersPeriodically(this, 1000));
        addBehaviour(new refreshWorkersOnMachinePeriodically(this, 1000));

        addBehaviour(new listener());
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
        myGui.close();
        // Printout a dismissal message
        out.println("MachineMaster "+getAID().getName()+" terminating.");
    }

    public void manageJob(String path, String[] input) {
        if(myGui.ACradiobutton.isSelected())
            out.println("MASTER: started processing job using Aho-Corasick");
        else
            out.println("MASTER: started processing job using standard find function");
        timestamp = nanoTime();
        partsToProcess = TextJobProcessor.loadParts(path, input, myGui.ACradiobutton.isSelected());
        partsProcessed = new ArrayList<TextJobPart>();
        for (TextJobPart part : partsToProcess)
            part.setId(random.nextInt());
        jobProcessing = true;
        addBehaviour(new refreshWorkersOnPlatform());
        addBehaviour(new askWorkersForLocations());
        if(inmmemory) addBehaviour(new inviteWorkers());
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timestamp = nanoTime();
        addBehaviour(new sendPrepareProcessorRequest());
        addBehaviour(new sendPartsToWorkers());
        addBehaviour(new listener());
    }


    private void manageJobProcessed()
    {
        Collections.sort(partsProcessed);

        DataContainer.foundLines = new ArrayList<Integer>();
        DataContainer.wordIndexStart = new ArrayList<Integer>();
        DataContainer.wordIndexStop = new ArrayList<Integer>();

        fullText = new StringBuilder();
        out.println("MASTER: job done, parts sorted.");
        for(TextJobPart part : partsProcessed) {
            fullText.append(part.getLines().toString());

            DataContainer.foundLines.addAll(part.getResults());
            DataContainer.wordIndexStart.addAll(part.wordStart);
            DataContainer.wordIndexStop.addAll(part.wordLength);
        }
        DataContainer.TextToParse = fullText.toString();
        long processingTime = nanoTime() - timestamp;
        double processingTimeSecs = processingTime / 1000000000.0;
        if(myGui.ACradiobutton.isSelected())
            out.println("MASTER: job finished wtih AhoCorasick after: " + processingTimeSecs + "s" );
        else
            out.println("MASTER: job finished wtih find function after: " + processingTimeSecs +"s" );
        resultsGui = new OutputGUI();
        resultsGui.showOutput();
        DataContainer.wipeOut();
    }


    private class sendPartsToWorkers extends Behaviour {
        public void action() {
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }
            ArrayList<AID> receivers;
            if(inmmemory)
                receivers = workersOnMachine;
            else
                receivers = workersOutsideMachine;
            int nrOfWorkers = receivers.size();
            if(nrOfWorkers == 0) return;
            int currentWorker = 0;
            for( TextJobPart part : partsToProcess)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                String content = "";
                request.setLanguage("text-jobs");
                request.setOntology("text-job-part-to-process");
                request.addReceiver(receivers.get(currentWorker));
                try {
                    content = Serializer.toString(part);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request.setConversationId("text-jobs-request");
                request.setReplyWith("request" + currentTimeMillis()); // Unique value
                request.setContent(content);
                myAgent.send(request);
                currentWorker++;
                if(currentWorker >= nrOfWorkers) currentWorker = 0;
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done() {
            return true;
        }
    }

    private class refreshWorkersOnPlatform extends Behaviour {
        public void action() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("text-jobs");
            sd.setName("text-jobs-worker");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                workersOnPlatform = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    workersOnPlatform[i] = result[i].getName();
                    out.println("Worker "+workersOnPlatform[i]);
                }
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        public boolean done() {
            return true;
        }
    }

    private class sendPrepareProcessorRequest extends Behaviour
    {
        public void action() {
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }

            for(AID workerAID : workersOnPlatform)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(workerAID);
                String content = null;
                request.setLanguage("text-jobs");
                request.setOntology("text-job-inivitation");
                try {
                    content = Serializer.toString(here());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request.setContent(content);
                myAgent.send(request);
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done() {
            return true;
        }
    }

    private class askWorkersForLocations extends Behaviour {
        public void action() {
            workersOnMachine = new ArrayList<AID>();
            workersOutsideMachine = new ArrayList<AID>();
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }
            for(AID workerAID : workersOnPlatform)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(workerAID);
                String content = null;
                request.setLanguage("text-jobs");
                request.setOntology("text-job-location-question");
                request.setContent(content);
                myAgent.send(request);
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done() {
            return true;
        }
    }

    private class refreshWorkersOnMachinePeriodically extends TickerBehaviour {
        public refreshWorkersOnMachinePeriodically(Agent a, long period) {super(a, period);}
            protected void onTick() {
                myAgent.addBehaviour(new askWorkersForLocations());
            }
    }

    private class refreshWorkersPeriodically extends TickerBehaviour
    {
        public refreshWorkersPeriodically(Agent a, long period) {super(a,period);}
        protected void onTick(){myAgent.addBehaviour(new refreshWorkersOnPlatform());}
    }

    private class inviteWorkers extends Behaviour
    {
        public void action() {
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }

            for(AID workerAID : workersOnPlatform)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(workerAID);
                String content = null;
                request.setLanguage("text-jobs");
                request.setOntology("text-job-inivitation");
                try {
                    content = Serializer.toString(here());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request.setContent(content);
                myAgent.send(request);
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done() {
            return true;
        }
    }

    private class listener extends Behaviour {
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if (msg != null  && msg.getLanguage().equals("text-jobs")) {
                String ont = msg.getOntology();
                String content;
                switch (ont)
                {
                    case "text-jobs-job-result":
                        out.println("MASTER: Result received");
                        break;

                    case "text-job-inivitation-accepted":
                        break;

                    case "text-job-processed-part":
                        if(!jobProcessing) break; // TODO send message to Worker, that there is no job processing.
                        content = msg.getContent();
                        TextJobPart part = null;
                        try {
                            part = (TextJobPart)Serializer.fromString(content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        out.println("MASTER: Part received: " +  part.getResults());
                        partsProcessed.add(part); // TODO verify part is correctly processed.
                        if(checkJobProcessed()) manageJobProcessed();
                        break;

                    case "text-job-location-question-response":
                        content = msg.getContent();
                        Location receivedLocation = null;
                        try {
                            receivedLocation = (Location)Serializer.fromString(content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if(receivedLocation.equals(here())) workersOnMachine.add(msg.getSender());
                        else workersOutsideMachine.add(msg.getSender());
                        break;

                    default:
                        out.println("MASTER: Unknown message received");
                }
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done()
        {
            return true;
        }
    }

    private boolean checkJobProcessed() {
        // TODO: check more precissly.
        return partsProcessed.size() == partsToProcess.size();

    }



    private class processJob extends Behaviour {

        public void action() {
        }

        public boolean done() {
                return true;
        }

    }


    public boolean isInmmemory() {
        return inmmemory;
    }

    public void setInmmemory(boolean inmmemory) {
        this.inmmemory = inmmemory;
    }



}

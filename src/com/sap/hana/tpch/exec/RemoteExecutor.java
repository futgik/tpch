package com.sap.hana.tpch.exec;

import com.jcraft.jsch.*;
import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.CommandExecutionException;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exception.FileDeliverException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 21/09/2014.
 * Allow to execute ssh command on remote computer and transfer files.
 * Using class factory.
 */
public class RemoteExecutor {

    private static SessionManager sm = new SessionManager();

    /**
     * Implement manager for keeping all sessions to remote machine.
     * And allow to get session to remote machine.
     */
    private static class SessionManager{
        List<Session> sessions;
        private JSch jSch = new JSch();

        public SessionManager(){
            try {
                jSch.setKnownHosts("known_hosts");
//  //connection without checking host key
//            java.util.Properties config = new java.util.Properties();
//            config.put("StrictHostKeyChecking", "no");
//            sshSession.setConfig(config);
            }
            catch(JSchException e){
                e.printStackTrace(); //todo
            }
            sessions = new ArrayList<Session>();
        }

        /**
         * Get session to remote machine.
         * @return session to remote machine.
         */
        public Session getSession(){
            Session newSession = null;
            try {
                newSession = jSch.getSession(Configurations.SSH_USER, Configurations.HOST, Configurations.SSH_PORT);
                newSession.setPassword(Configurations.SSH_PWD);
                newSession.connect();
                sessions.add(newSession);
                return newSession;
            } catch (JSchException e) {
                e.printStackTrace();
                if(newSession != null) closeSession(newSession);
                return null;
            }
        }

        /**
         * Close specified session.
         * @param session session to remote machine.
         */
        public void closeSession(Session session){
            if(session.isConnected()) session.disconnect();
            sessions.remove(session);
        }

        /**
         * Close all sessions to remote machine.
         */
        public void closeAll(){
            for(Session s : sessions){
                if(s.isConnected()) s.disconnect();
                sessions.remove(s);
            }
        }
    }

    public enum OutputStreamType{
        OutputStream,
        ErrorStream,
    }

    /**
     * Contains methods to execute command via ssh on remote machine.
     */
    public static class RemoteCommandExecutor{

        List<ActionListener> startExecutionListeners;
        List<ActionListener> processExecutionListeners;
        List<ActionListener> endExecutionListeners;

        //type of stream which using to output server result
        OutputStreamType outputStreamType = OutputStreamType.OutputStream;

        /**
         * default constructor. Create new listeners pool.
         */
        protected RemoteCommandExecutor(){
            startExecutionListeners = new LinkedList<>();
            processExecutionListeners = new LinkedList<>();
            endExecutionListeners = new LinkedList<>();
        }

        protected RemoteCommandExecutor(OutputStreamType streamType){
            this();
            outputStreamType = streamType;
        }

        /**
         * Add listener executing before start of executing command.
         * @param listener executing before start of executing command.
         */
        public void addStartExecutionListeners(ActionListener listener){
            if(!startExecutionListeners.contains(listener))
                startExecutionListeners.add(listener);
        }

        /**
         * Remove listener executing before start of executing command.
         * @param listener executing before start of executing command.
         */
        public void removeStartExecutionListener(ActionListener listener){
            startExecutionListeners.remove(listener);
        }

        /**
         * Add listener executing in process of executing command.
         * @param listener executing in process of executing command.
         */
        public void addProcessExecutionListeners(ActionListener listener){
            if(!processExecutionListeners.contains(listener))
                processExecutionListeners.add(listener);
        }

        /**
         * Remove listener executing in process of executing command.
         * @param listener executing in process of executing command.
         */
        public void removeProcessExecutionListener(ActionListener listener){
            processExecutionListeners.remove(listener);
        }

        /**
         * Add listener executing after executing remote command.
         * @param listener listener executing after executing remote command.
         */
        public void addEndExecutionListeners(ActionListener listener){
            if(!endExecutionListeners.contains(listener))
                endExecutionListeners.add(listener);
        }

        /**
         * Add listener executing after executing remote command.
         * @param listener listener executing after executing remote command.
         */
        public void removeEndExecutionListener(ActionListener listener){
            endExecutionListeners.remove(listener);
        }

        /**
         * Implement SSHPrecessMonitor with listener execution.
         */
        private class CommandExecProgressMonitor implements SSHProcessMonitor{
            SSHProcessMonitor userDeliverProgressMonitor = null;

            public CommandExecProgressMonitor() {}

            public CommandExecProgressMonitor(SSHProcessMonitor userProcessMonitor) {
                this.userDeliverProgressMonitor = userProcessMonitor;
            }

            @Override
            public void init(String executionCommand) {
                for(ActionListener al : startExecutionListeners) al.actionPerformed(new ActionEvent(this,0,executionCommand)); //execution start event notifier
                if(userDeliverProgressMonitor!=null) userDeliverProgressMonitor.init(executionCommand);
            }

            @Override
            public void process(String processOutput) {
                if(userDeliverProgressMonitor!=null) userDeliverProgressMonitor.process(processOutput);
                for(ActionListener al : processExecutionListeners) al.actionPerformed(new ActionEvent(this,0,processOutput)); //execution process event notifier
            }

            @Override
            public void end(String fullOutput) {
                if(userDeliverProgressMonitor!=null)
                    userDeliverProgressMonitor.end(fullOutput);
                for(ActionListener al : endExecutionListeners) al.actionPerformed(new ActionEvent(this,0,fullOutput)); //execution end event notifier
            }
        }

        /**
         * Execute command on remote computer. Execute via ssh.
         * @param command command to execute.
         * @return execution result.
         * @throws ExecutionException
         */
        public String execCommand(String command) throws ExecutionException{
            return execCommand(command, new CommandExecProgressMonitor());
        }

        /**
         * Execute command on remote computer. Execute via ssh.
         * @param command command to execute.
         * @param monitor visualizer of command execution process.
         * @return
         * @throws ExecutionException
         */
        public String execCommand(String command, SSHProcessMonitor monitor) throws ExecutionException{
            return execCommand(command, new CommandExecProgressMonitor(monitor));
        }

        /**
         * Execute command on remote computer.
         * Execute via ssh.
         * @param command command to execute.
         * @param monitor visualizer of command execution process.
         * @return result of execution.
         */
        public String execCommand(String command, CommandExecProgressMonitor monitor) throws ExecutionException{
            Session sshSession = sm.getSession();
            if(sshSession == null) throw new ExecutionException("Can't start ssh session with server. Check connection params.");
            String output = "";
            ChannelExec sshChannel = null;
            try{
                sshChannel = (ChannelExec)sshSession.openChannel("exec");
                sshChannel.setCommand(command);
                InputStream errStream = sshChannel.getErrStream();
                InputStream in = sshChannel.getInputStream();
                sshChannel.connect();
                monitor.init(command);
                switch (outputStreamType){
                    case OutputStream: output = toString(in,sshChannel,monitor); break;
                    case ErrorStream: output = toString(errStream, sshChannel, monitor); break;
                }
                if(sshChannel.getExitStatus() != 0){
                    throw new CommandExecutionException(getError(in),sshChannel.getExitStatus());
                }
                sshChannel.disconnect();
                sm.closeSession(sshSession);
            }
            catch (IOException e){
                throw new ExecutionException("Can't get input stream");
            }
            catch (JSchException e){
                throw new ExecutionException("Can't open execution channel",e);
            }
            finally {
                if(sshChannel != null && sshChannel.isConnected()) sshChannel.disconnect();
                sm.closeSession(sshSession);
            }
            return output;
        }

        /**
         * Execute command with sudo rights on remote computer.
         * @param command command to execute.
         * @return result of execution.
         * @throws ExecutionException
         */
        public String execSudoCommand(String command) throws ExecutionException{
            return execSudoCommand(command, new CommandExecProgressMonitor());
        }

        /**
         * Execute command with sudo rights on remote computer.
         * @param command command to execute.
         * @param monitor implementation of SSHProcessMonitor class.
         * @return result of execution.
         * @throws ExecutionException
         */
        public String execSudoCommand(String command, SSHProcessMonitor monitor) throws ExecutionException{
            return execSudoCommand(command, new CommandExecProgressMonitor(monitor));
        }

        /**
         * Execute command with sudo rights on remote computer.
         * Execute via ssh
         * @param command command to execute.
         * @return result of execution.
         * @throws ExecutionException
         */
        public String execSudoCommand(String command, CommandExecProgressMonitor monitor) throws ExecutionException{
            command = "sudo -S -p '' "+command;
            Session sshSession = sm.getSession();
            if(sshSession == null) throw new ExecutionException("Can't start ssh session with server. Check connection params.");
            ChannelExec sshChannel = null;
            String output = "";
            try{
                sshChannel = (ChannelExec)sshSession.openChannel("exec");
                sshChannel.setCommand(command);
                InputStream errStream = sshChannel.getErrStream();
                InputStream in = sshChannel.getInputStream();
                OutputStream out = sshChannel.getOutputStream();
                sshChannel.connect();
                out.write((Configurations.SSH_PWD + "\n").getBytes());
                out.flush();
                monitor.init(command);
                switch (outputStreamType){
                    case OutputStream: output = toString(in,sshChannel,monitor); break;
                    case ErrorStream: output = toString(errStream, sshChannel, monitor); break;
                }
                if(sshChannel.getExitStatus() != 0) {
                    output = getError(errStream);
                    throw new CommandExecutionException(output,sshChannel.getExitStatus());
                }
                sshChannel.disconnect();
                sm.closeSession(sshSession);
            }
            catch (IOException e){
                throw new ExecutionException("Can't get stream", e);
            }
            catch (JSchException e){
                throw  new ExecutionException("Can't execute command on server", e);
            }
            finally {
                if(sshChannel != null && sshChannel.isConnected()) sshChannel.disconnect();
                sm.closeSession(sshSession);
            }
            return output;
        }

        /**
         * return string output from server error stream.
         * @param errStream stream from where read data.
         * @return string output from server error stream.
         */
        private String getError(InputStream errStream){
            StringBuilder stringBuffer = new StringBuilder();
            if(errStream != null){
                try{
                    byte buffer[] = new byte[1024];
                    while (errStream.available() > 0){
                        int i = errStream.read(buffer,0,buffer.length);
                        if(i<0) break;
                        stringBuffer.append(new String(buffer,0,i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return stringBuffer.toString();
        }

        /**
         * waiting while process of execution end at the same time read server input stream.
         * Return string which contain messages from server input stream.
         * @param in Stream connected to server input stream.
         * @param channel ssh connection channel.
         * @param monitor allow to watch at the process of execution.
         * @return string which contain messages from server input stream.
         */
        private String toString(InputStream in, ChannelExec channel, SSHProcessMonitor monitor){
            StringBuilder stringBuffer = new StringBuilder();
            if (in != null) {
                try {
                    byte buffer[] = new byte[1024];
                    while(true) {
                        while (in.available() > 0) {
                            int i = in.read(buffer, 0, buffer.length);
                            if (i < 0) break;
                            String interimVal = new String(buffer, 0, i);
                            stringBuffer.append(interimVal);
                            monitor.process(interimVal);
                        }
                        if (channel.isClosed()) {
                            if (in.available() > 0) continue;
                            System.out.println("exit-status: "+channel.getExitStatus());
                            break;
                        }
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace(); //todo
                }
            }
            String result = stringBuffer.toString();
            monitor.end(result);
            return result;
        }
    }

    /**
     * Contains methods to deliver files to remote machine via ssh.
     */
    public static class RemoteDeliver{

        List<ActionListener> startExecutionListeners;
        List<ActionListener> processExecutionListeners;
        List<ActionListener> endExecutionListeners;

        protected RemoteDeliver(){
            startExecutionListeners = new LinkedList<>();
            processExecutionListeners = new LinkedList<>();
            endExecutionListeners = new LinkedList<>();
        }

        /**
         * Add listener executing before start of file transferring.
         * @param listener Listener executing before start of file transferring.
         */
        public void addStartExecutionListeners(ActionListener listener){
            if(!startExecutionListeners.contains(listener))
                startExecutionListeners.add(listener);
        }

        /**
         * Remove listener executing before start of file transferring.
         * @param listener Listener executing before start of file transferring.
         */
        public void removeStartExecutionListener(ActionListener listener){
            startExecutionListeners.remove(listener);
        }

        /**
         * Add listener executing in process of file transferring.
         * @param listener executing in process of file transferring.
         */
        public void addProcessExecutionListeners(ActionListener listener){
            if(!processExecutionListeners.contains(listener))
                processExecutionListeners.add(listener);
        }

        /**
         * Remove listener executing in process of file transferring.
         * @param listener executing in process of file transferring.
         */
        public void removeProcessExecutionListener(ActionListener listener){
            processExecutionListeners.remove(listener);
        }

        /**
         * Add listener executing after end of file transferring.
         * @param listener executing after end of file transferring.
         */
        public void addEndExecutionListeners(ActionListener listener){
            if(!endExecutionListeners.contains(listener))
                endExecutionListeners.add(listener);
        }

        /**
         * Add listener executing after end of file transferring.
         * @param listener executing after end of file transferring.
         */
        public void removeEndExecutionListener(ActionListener listener){
            endExecutionListeners.remove(listener);
        }

        /**
         * Implement sftpProgressMonitor, with executing events.
         */
        private class SFTPDeliverProgressMonitor implements SftpProgressMonitor{
            SftpProgressMonitor userDeliverProgressMonitor = null;

            public SFTPDeliverProgressMonitor() {}

            public SFTPDeliverProgressMonitor(SftpProgressMonitor userDeliverProgressMonitor) {
                this.userDeliverProgressMonitor = userDeliverProgressMonitor;
            }

            @Override
            public void init(int op, String src, String dest, long max) {
                for(ActionListener al : startExecutionListeners) al.actionPerformed(new ActionEvent(this,0,Long.toString(max))); //execution start event notifier
                if(userDeliverProgressMonitor!=null) userDeliverProgressMonitor.init(op,src,dest,max);
            }

            @Override
            public boolean count(long bytes) {
                boolean result = true;
                if(userDeliverProgressMonitor!=null)
                    result = userDeliverProgressMonitor.count(bytes);
                for(ActionListener al : processExecutionListeners) al.actionPerformed(new ActionEvent(this,0,Long.toString(bytes))); //execution process event notifier
                return result;
            }

            @Override
            public void end() {
                if(userDeliverProgressMonitor!=null)
                    userDeliverProgressMonitor.end();
                for(ActionListener al : endExecutionListeners) al.actionPerformed(new ActionEvent(this,0,"")); //execution end event notifier
            }
        }

        /**
         * Deliver file on remote computer to directory SERVER_WORK_DIR
         * @param filePath file to deliver
         */
        public void deliverFile(String filePath)throws FileDeliverException{
            deliverFile(filePath, new SFTPDeliverProgressMonitor());
        }

        /**
         * Deliver file on remote computer to directory SERVER_WORK_DIR
         * @param filePath file to deliver.
         * @param progressMonitor deliver monitor which implement SFTPDeliverProgressMonitor.
         * @throws FileDeliverException
         */
        private void deliverFile(String filePath, SFTPDeliverProgressMonitor progressMonitor) throws FileDeliverException{
            ChannelSftp channel = null;
            Session sshSession = sm.getSession();
            if(sshSession == null) throw new FileDeliverException("Can't start ssh session with server. Check connection params.");
            try {
                channel = (ChannelSftp) sshSession.openChannel("sftp");
                channel.connect();
                channel.put(filePath, Configurations.SERVER_WORK_DIR, progressMonitor, ChannelSftp.OVERWRITE);
                channel.disconnect();
                sm.closeSession(sshSession);
            } catch (SftpException e) {
                throw new FileDeliverException("Can't put file to server",e);
            } catch (JSchException e) {
                throw new FileDeliverException("Can't start sftp connection",e);
            } finally {
                if(channel != null && channel.isConnected()) channel.disconnect();
                sm.closeSession(sshSession);
            }
        }

        /**
         * Deliver file on remote computer to directory SERVER_WORK_DIR
         * @param filePath file to deliver.
         * @param progressMonitor deliver monitor which implement SftpProgressMonitor.
         * @throws FileDeliverException
         */
        public void deliverFile(String filePath, SftpProgressMonitor progressMonitor) throws FileDeliverException{
            deliverFile(filePath, new SFTPDeliverProgressMonitor(progressMonitor));
        }
    }

    /**
     * Implement transfer file progress monitor with using console.
     */
    private static class ConsoleProgressMonitor implements SftpProgressMonitor{
        long count=0;
        long max=0;
        private long percent=-1;
        int fullPrintCount = 0;

        public ConsoleProgressMonitor() {;}

        @Override
        public void init(int op, String src, String dest, long max) {
            this.max = max;
            System.out.println("STARTING: "+op+" "+src+" -> "+dest+" total: "+max);
        }

        @Override
        public boolean count(long bytes) {
            count+=bytes;
            int newPrintCount = (int)((count*100)/max);
            int currentPrintCount =newPrintCount-fullPrintCount;
            for(int i=0;i<currentPrintCount;i++)
                System.out.print("#");
            fullPrintCount = newPrintCount;
            return(true);
        }
        @Override
        public void end() {
            System.out.println("\nFINISHED!");
        }
    }

    /**
     * Return command executor for execute command via ssh on remote machine.
     * @return command executor.
     */
    public static RemoteCommandExecutor getRemoteCommandExecutor(){
        return new RemoteCommandExecutor();
    }

    /**
     * Return command executor witch allow to execute command via ssh on remote machine.
     * @param streamType Type of stream which using on server to output process result;
     * @return command executor.
     */
    public static RemoteCommandExecutor getRemoteCommandExecutor(OutputStreamType streamType){
        return new RemoteCommandExecutor(streamType);
    }

    /**
     * Return file deliver object to transfer file on remote machine over ssh.
     * @return file deliver object.
     */
    public static RemoteDeliver getRemoteDeliver(){
        return new RemoteDeliver();
    }

    public static void main(String[] arg) {
        try {
            getRemoteCommandExecutor().execCommand("echo \"hello world\"",new SSHProcessMonitor() {
                @Override
                public void init(String executionCommand) {
                    System.out.println("Executed command: " + executionCommand);
                }

                @Override
                public void process(String processOutput) {
                    System.out.println(processOutput);
                }

                @Override
                public void end(String fullOutput) {
                    System.out.println("End");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

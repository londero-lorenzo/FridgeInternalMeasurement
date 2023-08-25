package ArduinoSampler.arduino;

public enum ServerCommands {

    NOT_RECOGNIZED_COMMAND{
        @Override
        public byte getByte() {
            return '!';
        }
    },

    INITIAL_IDENTIFIER{

        @Override
        public byte getByte(){
            return '1';
        }
    },
    WATCHWORD_REQUIRED__IDENTIFIER_CHAR{
        @Override
        public byte getByte() {
            return 'w';
        }

    },

    COMMANDS_AUTHORIZED{
        @Override
        public byte getByte(){
            return '2';
        }
    },

    COMMANDS_NOT_AUTHORIZED{
        @Override
        public byte getByte(){
            return '3';
        }
    },

    CREATE_TASK{
        @Override
        public byte getByte(){
            return '4';
        }
    },

    DELETE_TASK{
        @Override
        public byte getByte(){
            return '5';
        }
    },

    GET_DATA_FROM_BUFFER{
        @Override
        public byte getByte(){
            return '6';
        }
    };

    public static ServerCommands fromCommandByte(int commandReceived) {
        for (ServerCommands command : ServerCommands.values())
            if (command.matchTo((byte) commandReceived))
                return command;
        return NOT_RECOGNIZED_COMMAND;
    }

    public abstract byte getByte();

    public boolean matchTo(byte receivedCommand){
        return this.getByte() == receivedCommand;
    }
}

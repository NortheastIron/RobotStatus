# RobotStatus
Robot checking http status
1. Download "Database.rar", unpack and install on your computer
2. Place the file "StatusRobotM.jar" and folder "lib" in a single directory
3. To run the program, type the following command at a command prompt "java -jar StatusRobotM.jar YYYY-MM-DD"

The program does not display any information after entering 3 key values

Decryption of invalid statuses:

    Invalid HTTP Response = -1;
    Malformed URL = -999;
    Status check timeout = -998;
    Unexpected exception = -997;
    Status request = 0; (This status value can be fixed URL if during status request the program shuts down)
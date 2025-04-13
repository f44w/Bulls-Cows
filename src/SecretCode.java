class SecretCode {
    protected String decipheredCode; // game code
    protected char codeType; // char to identify game code type

    public String getCode() { return decipheredCode; }

    public void setCode(String code) {
        this.decipheredCode = code;
    }
}
public class Mix {
    private String name;
    private double forestVolume;
    private double rainVolume;
    private double windVolume;

    public Mix(String name, double forestVolume, double rainVolume, double windVolume) {
        this.name = name;
        this.forestVolume = forestVolume;
        this.rainVolume = rainVolume;
        this.windVolume = windVolume;
    }

    public String getName() {
        return name;
    }

    public double getForestVolume() {
        return forestVolume;
    }

    public double getRainVolume() {
        return rainVolume;
    }

    public double getWindVolume() {
        return windVolume;
    }
}

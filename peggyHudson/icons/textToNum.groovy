f = new File("/home/glloyd/projects/applications/peggy/icons/lightning-binary.txt")
f.setReadable(true, false)

lines = f.readLines()
nums = {}

lines.each{ line ->
  i = 0
  runningTot = 0;
  line.reverse().getChars().each { x ->
    xAsNum = Integer.parseInt(x.toString())
    runningTot += xAsNum * (2 ** i)
    i++
  }
  println runningTot
}

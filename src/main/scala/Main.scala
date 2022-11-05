import java.sql.{Connection, Date, DriverManager, ResultSet, Statement}
import java.util
import java.util.Calendar

object Main {

  def clearData(stm : Statement) = {
    stm.executeUpdate("delete from department;")
    stm.executeUpdate("delete from grade;")
    stm.executeUpdate("delete from employee;")
  }

  import java.text.DateFormat
  import java.text.SimpleDateFormat
  import java.util.TimeZone

  def toISO8601UTC(date: util.Date): String = {
    val tz = TimeZone.getTimeZone("UTC")
    val df = new SimpleDateFormat("yyyy-MM-dd")
    df.setTimeZone(tz)
    df.format(date)
  }

  def generateData(conn: Connection) = {
    val rand = new scala.util.Random
    var empId = 0
    val currentDate = new util.Date()
    val MILLIS_IN_A_DAY: Long = 1000 * 60 * 60 * 24
    for (i <- 1 until(10)) {
      val st = conn.prepareStatement(
        "insert into department (id, name, head_of_department, number_of_employees) values (?, ?, ?, ?);")
      st.setInt(1, i)
      st.setString(2, s"department #$i")
      st.setString(3, s"head of department #$i")
      val num_of_epm = rand.between(2, 10)
      st.setInt(4, num_of_epm)
      st.executeUpdate()
      val depId = i
      for (i <- 1 to num_of_epm) {
        val st = conn.prepareStatement(
          "insert into employee (id, name, start_date, position, level, salary, department_id, has_access) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?)")

        def randomStartDate: Date  = {
          var delta = rand.between(30, 1500)
          val d = toISO8601UTC(new java.util.Date(currentDate.getTime - MILLIS_IN_A_DAY * delta))
          println(s"$d, $delta, ${MILLIS_IN_A_DAY * delta}, $MILLIS_IN_A_DAY")
          Date.valueOf(d)
        }
        st.setInt(1, empId)
        st.setString(2, s"employee #$i of dep #$depId")
        val rDate = randomStartDate
        st.setDate(3, randomStartDate)
        def getRandomEmpLevel(forceLead : Boolean) = {
          val lvl = rand.nextInt(8)
          if (forceLead) {
            "lead"
          } else if (lvl < 2) {
            "jun"
          } else if (lvl < 6) {
            "middle"
          } else {
            "senior"
          }
        }
        st.setString(4, "employee")
        val level = getRandomEmpLevel(i == 1)
        st.setString(5, level)
        val salary = level match {
          case "lead" => rand.between(200000, 400000)
          case "jun" => rand.between(35000, 50000)
          case "middle" => rand.between(75000, 125000)
          case "senior" => rand.between(125000, 200000)
          case _ => 0
        }
        st.setInt(6, salary)
        st.setInt(7, depId)
        st.setBoolean(8, rand.nextInt(100) > 80 || level == "lead")
        st.executeUpdate()
        var prevYear = Calendar.getInstance().get(Calendar.YEAR) - 1
        def getRandomGrade : Char = {
          rand.nextInt(25) match {
            case x if 0 to 3 contains x => 'E'
            case x if 4 to 8 contains x => 'D'
            case x if 9 to 16 contains x => 'C'
            case x if 17 to 20 contains x => 'B'
            case _ => 'A'


          }
        }

        for (i <- 1 to 4) {
          val st = conn.prepareStatement(
            "insert into grade (employee_id, year, quarter, grade) VALUES (?, ?, ?, ?);")
          st.setInt(1, empId);
          st.setInt(2, prevYear)
          st.setInt(3, i)
          st.setString(4, getRandomGrade.toString)
          st.executeUpdate()
        }

        empId += 1
      }
    }
  }


  def main(args: Array[String]): Unit = {
    println("Hello world!")

    classOf[org.postgresql.Driver]
    val con_str = "jdbc:postgresql://localhost:5431/pg_practice_1?user=postgres&password=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
      clearData(stm)
      generateData(conn)
      val rs = stm.executeQuery("SELECT * from department")
      println("ok 4")

      while (rs.next) {
        println(rs.getString("name"))
      }
    } catch {
      case e: Exception => println(s"error $e")
    }
    finally {
      conn.close
    }

  }
}
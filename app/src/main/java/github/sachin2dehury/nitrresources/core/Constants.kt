package github.sachin2dehury.nitrresources.core

import java.text.DecimalFormat

const val REQUEST_CODE_SIGN_IN = 9100
const val REQUEST_CODE_OPEN_FILE = 9200
const val REQUEST_CODE_DOWNLOAD_LINK = 9300
const val REQUEST_CODE_OPEN_LINK = 9400

const val STREAM_LIST = 100
const val B_ARCH_LIST = 200
const val B_TECH_LIST = 201
const val INT_MSC_LIST = 202
const val MSC_LIST = 203
const val M_TECH_LIST = 204
const val YEAR_LIST = 300
const val NOTES_LIST = 400
const val ASSIGNMENT_LIST = 401
const val SLIDES_LIST = 402
const val LAB_LIST = 403
const val NO_LIST = 0

const val college = "NITR"
const val BOOK_LINK = "http://libgen.rs/"
const val QUESTION_LINK = "https://eapplication.nitrkl.ac.in/nitris/Login.aspx"
const val MAIL_LINK = "https://mail.nitrkl.ac.in/"
const val TELEGRAM_NEWS_LINK = "https://t.me/s/nitrkl"
const val PDF = "application/pdf"
const val PPT = "application/ppt"
const val IMG = "image/*"
const val MB = 1024 * 1024
const val ALL = "*/*"

val format = DecimalFormat("#.##")

val branch = listOf(
    "AR",
    "CY", "ER", "LS", "MA", "PH",
    "BM", "BT", "CE", "CH", "CR", "CS", "EC", "EI", "EE", "FP", "ID", "ME", "MM", "MN"
)
val streamYears = listOf(5, 4, 3, 2, 2)
val noList = listOf("No Data Available!")
val pages = listOf("Notes", "Assignments", "Slides", "Labs")
val streams = listOf("B. Arch", "B. Tech", "Int. M, Sc (Only B. Sc)", "M. Sc", "M. Tech")
val years = listOf("First Year", "Second Year", "Third Year", "Fourth Year", "Fifth Year")
val bArch = listOf("Architecture")
val bTech = listOf(
    "Biomedical Engineering",
    "Biotechnology",
    "Civil Engineering",
    "Chemical Engineering",
    "Ceramic Engineering",
    "Computer Science and Engineering",
    "Electronics and Communication Engineering",
    "Electronics and Instrumentation Engineering",
    "Electrical Engineering",
    "Food Process Engineering",
    "Industrial Design",
    "Mechanical Engineering",
    "Metallurgical and Materials Engineering",
    "Mining Engineering"
)
val mTech = listOf(
    "Biomedical Engineering",
    "Biotechnology",
    "Geotechnical Engineering",
    "Structural Engineering",
    "Transportation Engineering",
    "Water Resources Engineering",
    "Chemical Engineering",
    "Safety Engineering",
    "Energy and Environmental Engineering",
    "Ceramic Engineering",
    "Industrial Ceramics",
    "Computer Science",
    "Information Security",
    "Software Engineering",
    "Analytics and Decision Sciences",
    "Telematics and Signal Processing",
    "VLSI Design and Embedded Systems",
    "Electronics and Instrumentation Engineering",
    "Communication and Signal Processing",
    "Communication and Networks",
    "Signal and Image Processing",
    "Microwave and Radar Engineering",
    "Electronic Systems and Communication",
    "Power Control and Drives",
    "Control and Automation",
    "Power Electronics and Drives",
    "Industrial Electronics",
    "Power Systems Engineering",
    "Atmosphere and Ocean Science",
    "Food Process Engineering",
    "Industrial Design",
    "Machine Design and Analysis",
    "Production Engineering",
    "Thermal Engineering",
    "Cryogenic and Vacuum Technology",
    "Plastics, Composites and Timber Engineering",
    "Metallurgical and Materials Engineering",
    "Steel Technology",
    "Mining Engineering"
)
val msc = listOf(
    "Chemistry",
    "Applied Geology",
    "Atmospheric Sciences",
    "Life Science",
    "Mathematics",
    "Physics"
)
val intMsc = listOf(
    "Chemistry",
    "Life Science",
    "Mathematics",
    "Physics"
)
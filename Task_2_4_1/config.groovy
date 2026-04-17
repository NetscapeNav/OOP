//include 'tasks_2026.groovy'

tasks {
    task('1_1') {
        name = "Самая первая лаба"
        maxScore = 10
        softDeadline = "2026-02-01"
        hardDeadline = "2026-02-15"
    }

    task('2_1_1') {
        name = "Простые числа"
        maxScore = 200
        softDeadline = "2026-03-01"
        hardDeadline = "2026-03-15"
    }
}

groups {
    group(676767) {
        student('kavaka') {
            name = "Кавака Арбандзургинджатов"
            repoURL = "https://github.com/NetscapeNav/OOP.git"
        }
    }
}

assignments {
    assign "1_1" to 676767
    assign "2_1_1" to 676767
}

checkpoints {
    point 'КР1', '2026-04-01'
}

settings {
    timeout = 30
    styleGuide = "GOOGLE"
    convertion 10, 3
    convertion 50, 4
    convertion 100, 5
}

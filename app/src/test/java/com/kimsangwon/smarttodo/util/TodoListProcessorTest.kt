package com.kimsangwon.smarttodo.util

import com.kimsangwon.smarttodo.data.model.Todo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class TodoListProcessorTest {

    private val zoneId = ZoneId.of("Asia/Seoul")
    private val today = LocalDate.of(2026, 7, 20)

    private fun toMillis(date: LocalDate): Long {
        return date
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    private fun process(
        todos: List<Todo>,
        searchText: String = "",
        selectedFilter: String = "전체"
    ): List<Todo> {
        return processTodoList(
            todos = todos,
            searchText = searchText,
            selectedFilter = selectedFilter,
            today = today,
            zoneId = zoneId
        )
    }

    @Test
    fun 검색어와_일치하는_할_일만_표시한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "영어 공부"
            ),
            Todo(
                id = 2,
                title = "운동하기"
            ),
            Todo(
                id = 3,
                title = "영어 단어 복습"
            )
        )

        val result = process(
            todos = todos,
            searchText = "영어"
        )

        assertEquals(2, result.size)
        assertTrue(
            result.all {
                it.title.contains("영어")
            }
        )
    }

    @Test
    fun 검색은_영문_대소문자를_구분하지_않는다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "Study English"
            ),
            Todo(
                id = 2,
                title = "Exercise"
            )
        )

        val result = process(
            todos = todos,
            searchText = "english"
        )

        assertEquals(1, result.size)
        assertEquals(
            "Study English",
            result.first().title
        )
    }

    @Test
    fun 미완료_필터는_미완료_항목만_표시한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "미완료",
                isCompleted = false
            ),
            Todo(
                id = 2,
                title = "완료",
                isCompleted = true
            )
        )

        val result = process(
            todos = todos,
            selectedFilter = "미완료"
        )

        assertEquals(1, result.size)
        assertTrue(
            result.none {
                it.isCompleted
            }
        )
    }

    @Test
    fun 완료_필터는_완료된_항목만_표시한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "미완료",
                isCompleted = false
            ),
            Todo(
                id = 2,
                title = "완료",
                isCompleted = true
            )
        )

        val result = process(
            todos = todos,
            selectedFilter = "완료"
        )

        assertEquals(1, result.size)
        assertTrue(
            result.all {
                it.isCompleted
            }
        )
    }

    @Test
    fun 오늘_필터는_기한이_지났거나_오늘_마감인_미완료만_표시한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "어제 마감",
                dueDateMillis = toMillis(
                    today.minusDays(1)
                )
            ),
            Todo(
                id = 2,
                title = "오늘 마감",
                dueDateMillis = toMillis(today)
            ),
            Todo(
                id = 3,
                title = "내일 마감",
                dueDateMillis = toMillis(
                    today.plusDays(1)
                )
            ),
            Todo(
                id = 4,
                title = "오늘 완료",
                isCompleted = true,
                dueDateMillis = toMillis(today)
            ),
            Todo(
                id = 5,
                title = "날짜 없음"
            )
        )

        val result = process(
            todos = todos,
            selectedFilter = "오늘"
        )

        assertEquals(
            listOf(1, 2),
            result.map { it.id }
        )
    }

    @Test
    fun 전체_목록에서_완료된_항목은_마지막에_표시한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "완료 항목",
                isCompleted = true,
                priority = 3
            ),
            Todo(
                id = 2,
                title = "미완료 항목",
                isCompleted = false,
                priority = 1
            )
        )

        val result = process(todos)

        assertEquals(2, result.first().id)
        assertEquals(1, result.last().id)
    }

    @Test
    fun 미완료_항목은_기한_지남_오늘_미래_날짜없음_순으로_정렬한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "날짜 없음"
            ),
            Todo(
                id = 2,
                title = "미래",
                dueDateMillis = toMillis(
                    today.plusDays(3)
                )
            ),
            Todo(
                id = 3,
                title = "오늘",
                dueDateMillis = toMillis(today)
            ),
            Todo(
                id = 4,
                title = "기한 지남",
                dueDateMillis = toMillis(
                    today.minusDays(1)
                )
            )
        )

        val result = process(todos)

        assertEquals(
            listOf(4, 3, 2, 1),
            result.map { it.id }
        )
    }

    @Test
    fun 미래_마감일은_가까운_날짜부터_정렬한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "5일 뒤",
                dueDateMillis = toMillis(
                    today.plusDays(5)
                )
            ),
            Todo(
                id = 2,
                title = "내일",
                dueDateMillis = toMillis(
                    today.plusDays(1)
                )
            ),
            Todo(
                id = 3,
                title = "3일 뒤",
                dueDateMillis = toMillis(
                    today.plusDays(3)
                )
            )
        )

        val result = process(todos)

        assertEquals(
            listOf(2, 3, 1),
            result.map { it.id }
        )
    }

    @Test
    fun 마감일이_같으면_높은_우선순위가_먼저_표시된다() {
        val sameDate = toMillis(
            today.plusDays(2)
        )

        val todos = listOf(
            Todo(
                id = 1,
                title = "낮음",
                priority = 1,
                dueDateMillis = sameDate
            ),
            Todo(
                id = 2,
                title = "높음",
                priority = 3,
                dueDateMillis = sameDate
            ),
            Todo(
                id = 3,
                title = "보통",
                priority = 2,
                dueDateMillis = sameDate
            )
        )

        val result = process(todos)

        assertEquals(
            listOf(2, 3, 1),
            result.map { it.id }
        )
    }

    @Test
    fun 모든_조건이_같으면_최근에_추가된_ID가_먼저_표시된다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "첫 번째",
                priority = 2
            ),
            Todo(
                id = 3,
                title = "세 번째",
                priority = 2
            ),
            Todo(
                id = 2,
                title = "두 번째",
                priority = 2
            )
        )

        val result = process(todos)

        assertEquals(
            listOf(3, 2, 1),
            result.map { it.id }
        )
    }

    @Test
    fun 검색과_완료_필터를_동시에_적용할_수_있다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "영어 공부",
                isCompleted = true
            ),
            Todo(
                id = 2,
                title = "영어 단어",
                isCompleted = false
            ),
            Todo(
                id = 3,
                title = "운동",
                isCompleted = true
            )
        )

        val result = process(
            todos = todos,
            searchText = "영어",
            selectedFilter = "완료"
        )

        assertEquals(1, result.size)
        assertEquals(1, result.first().id)
    }

    @Test
    fun 결과가_없으면_빈_목록을_반환한다() {
        val todos = listOf(
            Todo(
                id = 1,
                title = "영어 공부"
            )
        )

        val result = process(
            todos = todos,
            searchText = "존재하지 않는 검색어"
        )

        assertTrue(result.isEmpty())
    }
}
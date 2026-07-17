package com.kimsangwon.smarttodo.util

import com.kimsangwon.smarttodo.data.model.Todo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TodoReminderTimeCalculatorTest {

    private val zoneId = ZoneId.of("Asia/Seoul")

    @Test
    fun 날짜와_시간이_있으면_정확한_밀리초를_반환한다() {
        val dueDate = LocalDate.of(
            2026,
            7,
            20
        )

        val dueDateMillis = dueDate
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        val todo = Todo(
            id = 1,
            title = "알림 테스트",
            dueDateMillis = dueDateMillis,
            dueHour = 18,
            dueMinute = 30
        )

        val expected = LocalDateTime.of(
            2026,
            7,
            20,
            18,
            30
        )
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        val actual = calculateReminderTimeMillis(
            todo = todo,
            zoneId = zoneId
        )

        assertEquals(
            expected,
            actual
        )
    }

    @Test
    fun 마감일이_없으면_null을_반환한다() {
        val todo = Todo(
            id = 2,
            title = "날짜 없음",
            dueDateMillis = null,
            dueHour = 10,
            dueMinute = 30
        )

        val result = calculateReminderTimeMillis(
            todo = todo,
            zoneId = zoneId
        )

        assertNull(result)
    }

    @Test
    fun 시간이_없으면_null을_반환한다() {
        val dueDate = LocalDate.of(
            2026,
            7,
            20
        )

        val todo = Todo(
            id = 3,
            title = "시간 없음",
            dueDateMillis = dueDate
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli(),
            dueHour = null,
            dueMinute = null
        )

        val result = calculateReminderTimeMillis(
            todo = todo,
            zoneId = zoneId
        )

        assertNull(result)
    }

    @Test
    fun 시가_범위를_벗어나면_null을_반환한다() {
        val dueDate = LocalDate.of(
            2026,
            7,
            20
        )

        val todo = Todo(
            id = 4,
            title = "잘못된 시",
            dueDateMillis = dueDate
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli(),
            dueHour = 24,
            dueMinute = 0
        )

        val result = calculateReminderTimeMillis(
            todo = todo,
            zoneId = zoneId
        )

        assertNull(result)
    }

    @Test
    fun 분이_범위를_벗어나면_null을_반환한다() {
        val dueDate = LocalDate.of(
            2026,
            7,
            20
        )

        val todo = Todo(
            id = 5,
            title = "잘못된 분",
            dueDateMillis = dueDate
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli(),
            dueHour = 12,
            dueMinute = 60
        )

        val result = calculateReminderTimeMillis(
            todo = todo,
            zoneId = zoneId
        )

        assertNull(result)
    }

    @Test
    fun 자정_시간도_정상적으로_계산한다() {
        val dueDate = LocalDate.of(
            2026,
            12,
            31
        )

        val todo = Todo(
            id = 6,
            title = "자정 테스트",
            dueDateMillis = dueDate
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli(),
            dueHour = 0,
            dueMinute = 0
        )

        val result = calculateReminderTimeMillis(
            todo = todo,
            zoneId = zoneId
        )

        val expected = dueDate
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        assertTrue(result != null)
        assertEquals(expected, result)
    }
}
package com.kimsangwon.smarttodo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DueDateFormatterTest {

    private val zoneId = ZoneId.of("Asia/Seoul")
    private val today = LocalDate.of(2026, 7, 20)

    private fun toMillis(date: LocalDate): Long {
        return date
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    @Test
    fun 마감일이_없으면_null을_반환한다() {
        val result = formatDueDate(
            dueDateMillis = null,
            dueHour = null,
            dueMinute = null,
            today = today
        )

        assertNull(result)
    }

    @Test
    fun 오늘_마감이면_오늘_마감으로_표시한다() {
        val result = formatDueDate(
            dueDateMillis = toMillis(today),
            dueHour = null,
            dueMinute = null,
            today = today
        )

        assertNotNull(result)
        assertEquals(
            "오늘 마감",
            result?.text
        )
        assertFalse(
            result?.isOverdue ?: true
        )
    }

    @Test
    fun 오늘_마감에_시간이_있으면_시간도_표시한다() {
        val result = formatDueDate(
            dueDateMillis = toMillis(today),
            dueHour = 18,
            dueMinute = 5,
            today = today
        )

        assertEquals(
            "오늘 마감 · 18:05",
            result?.text
        )
        assertFalse(
            result?.isOverdue ?: true
        )
    }

    @Test
    fun 내일_마감이면_내일_마감으로_표시한다() {
        val tomorrow = today.plusDays(1)

        val result = formatDueDate(
            dueDateMillis = toMillis(tomorrow),
            dueHour = 9,
            dueMinute = 30,
            today = today
        )

        assertEquals(
            "내일 마감 · 09:30",
            result?.text
        )
        assertFalse(
            result?.isOverdue ?: true
        )
    }

    @Test
    fun 일주일_이내이면_남은_날짜를_표시한다() {
        val dueDate = today.plusDays(3)

        val result = formatDueDate(
            dueDateMillis = toMillis(dueDate),
            dueHour = null,
            dueMinute = null,
            today = today
        )

        assertEquals(
            "3일 남음",
            result?.text
        )
        assertFalse(
            result?.isOverdue ?: true
        )
    }

    @Test
    fun 일주일을_초과하면_실제_날짜를_표시한다() {
        val dueDate = LocalDate.of(
            2026,
            8,
            10
        )

        val result = formatDueDate(
            dueDateMillis = toMillis(dueDate),
            dueHour = null,
            dueMinute = null,
            today = today
        )

        assertEquals(
            "2026년 8월 10일",
            result?.text
        )
        assertFalse(
            result?.isOverdue ?: true
        )
    }

    @Test
    fun 지난_날짜이면_기한_지남으로_표시한다() {
        val yesterday = today.minusDays(1)

        val result = formatDueDate(
            dueDateMillis = toMillis(yesterday),
            dueHour = 14,
            dueMinute = 0,
            today = today
        )

        assertEquals(
            "기한 지남 · 7월 19일 · 14:00",
            result?.text
        )
        assertTrue(
            result?.isOverdue == true
        )
    }

    @Test
    fun 시간의_분은_두자리로_표시한다() {
        val result = formatDueDate(
            dueDateMillis = toMillis(today),
            dueHour = 7,
            dueMinute = 3,
            today = today
        )

        assertEquals(
            "오늘 마감 · 07:03",
            result?.text
        )
    }
}
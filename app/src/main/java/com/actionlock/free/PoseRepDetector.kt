package com.actionlock.free

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2
import kotlin.math.abs

/**
 * MOD-02: On-Device AI Biomechanical Joint Angle Calculator for
 * Push-ups, Squats, Sit-ups, and Lunges using Google ML Kit Pose Detection.
 */
class PoseRepDetector(private val exerciseType: String) {

    var repCount: Int = 0
        private set
    private var isDownPhase: Boolean = false

    fun processPose(pose: Pose): Int {
        val angle = when (exerciseType) {
            "pushups" -> getJointAngle(
                pose,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_ELBOW,
                PoseLandmark.LEFT_WRIST
            )
            "squats", "lunges" -> getJointAngle(
                pose,
                PoseLandmark.LEFT_HIP,
                PoseLandmark.LEFT_KNEE,
                PoseLandmark.LEFT_ANKLE
            )
            "situps" -> getJointAngle(
                pose,
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.LEFT_HIP,
                PoseLandmark.LEFT_KNEE
            )
            else -> null
        } ?: return repCount

        if (angle < 95.0 && !isDownPhase) {
            isDownPhase = true
        } else if (angle > 155.0 && isDownPhase) {
            isDownPhase = false
            repCount++
        }
        return repCount
    }

    private fun getJointAngle(pose: Pose, firstId: Int, midId: Int, lastId: Int): Double? {
        val p1 = pose.getPoseLandmark(firstId)?.position ?: return null
        val p2 = pose.getPoseLandmark(midId)?.position ?: return null
        val p3 = pose.getPoseLandmark(lastId)?.position ?: return null

        val radians = atan2(p3.y - p2.y, p3.x - p2.x) - atan2(p1.y - p2.y, p1.x - p2.x)
        var angle = abs(radians * 180.0 / Math.PI)
        if (angle > 180.0) angle = 360.0 - angle
        return angle
    }
}

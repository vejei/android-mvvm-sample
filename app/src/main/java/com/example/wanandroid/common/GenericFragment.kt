package com.example.wanandroid.common

import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

abstract class GenericFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
}
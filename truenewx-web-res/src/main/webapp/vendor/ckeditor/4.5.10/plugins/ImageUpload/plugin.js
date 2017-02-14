CKEDITOR.plugins.add('ImageUpload', {
    init: function (editor) {
        var pluginName = 'ImageUpload';
        CKEDITOR.dialog.add(pluginName, this.path + 'dialogs/imageupload.js');
        editor.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
        editor.ui.addButton(pluginName,
        {
            label: '添加图片',
            command: pluginName,
            icon: this.path + 'images/image-upload.png'
        });
    }
});
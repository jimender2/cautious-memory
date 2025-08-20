package net.jimender2.ignition.pdf;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

class FilePathSetter implements Runnable {

    private final Viewer viewer;
    private final String filePath;

    /**
     * Sets the file path for the PDF viewer.
     * 
     * @param viewer The Viewer instance to set the file path for.
     * @param path   The file path to set.
     */
    FilePathSetter(Viewer viewer, String path) {
        this.viewer = viewer;
        this.filePath = path;
    }

    public void run() {
        boolean success = false;
        if (StringUtils.isNotBlank(this.filePath))
            success = this.viewer.tryOpen(this.filePath);

        if (!success)
            this.viewer.controller.closeDocument();

    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            FilePathSetter that = (FilePathSetter) o;
            return (new EqualsBuilder()).append(this.filePath, that.filePath).isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (new HashCodeBuilder(17, 37)).append(this.filePath).toHashCode();
    }
}